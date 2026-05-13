from fastapi import APIRouter
import re

from pydantic import BaseModel, Field
from sqlalchemy import text
from sqlalchemy.ext.asyncio import AsyncSession
from fastapi import Depends, HTTPException

from app.core.config import settings
from app.core.database import get_db

router = APIRouter()


class Nlp2SqlRequest(BaseModel):
    question: str = Field(min_length=3, max_length=280)


BLOCKED_TERMS = {
    "drop", "truncate", "delete", "update", "insert", "alter", "grant", "revoke", "pg_", "information_schema"
}


BLOCKED_SQL_PATTERNS = [
    r"\b(drop|truncate|delete|update|insert|alter|grant|revoke|create)\b",
    r";",
    r"--",
    r"/\*",
    r"\bpg_",
    r"\binformation_schema\b",
]

MONTH_MAP = {
    "janeiro": 1,
    "fevereiro": 2,
    "marco": 3,
    "março": 3,
    "abril": 4,
    "maio": 5,
    "junho": 6,
    "julho": 7,
    "agosto": 8,
    "setembro": 9,
    "outubro": 10,
    "novembro": 11,
    "dezembro": 12,
}


def _normalize(text_value: str) -> str:
    normalized = " ".join((text_value or "").strip().lower().split())
    # Tolerância para erros comuns de digitação
    normalized = normalized.replace("pacintes", "pacientes")
    return normalized


def _build_insights_plan(question: str, columns: list[str], rows: list[dict]) -> dict:
    normalized = _normalize(question)

    if not columns or not rows:
        return {
            "enabled": False,
            "reason": "Sem dados suficientes para correlação visual"
        }

    lower_cols = [str(col).lower() for col in columns]

    numeric_candidates = [
        columns[idx]
        for idx, col in enumerate(lower_cols)
        if any(token in col for token in ["total", "count", "qtd", "quant", "valor", "score", "percent"]) 
    ]

    if not numeric_candidates:
        numeric_candidates = [
            columns[idx]
            for idx, col in enumerate(lower_cols)
            if col != "id" and not col.endswith("_id")
        ]

    metric_field = numeric_candidates[0] if numeric_candidates else columns[-1]

    label_candidates = [
        columns[idx]
        for idx, col in enumerate(lower_cols)
        if any(token in col for token in ["nome", "procedimento", "dia", "mes", "mês", "categoria", "canal", "status", "etapa"])
    ]
    label_field = label_candidates[0] if label_candidates else next((c for c in columns if c != metric_field), metric_field)

    unique_labels = len({str(row.get(label_field, "")) for row in rows})
    if unique_labels <= 5:
        top_n = unique_labels
    elif unique_labels <= 12:
        top_n = 8
    else:
        top_n = 10

    suggested_charts = ["bar", "pie"]
    if "mes" in normalized or "mês" in normalized or "evolucao" in normalized or "evolução" in normalized:
        suggested_charts = ["line", "bar"]
    if "participacao" in normalized or "participação" in normalized:
        suggested_charts = ["pie", "bar"]

    explanation = (
        f"Plano automático: dimensão '{label_field}', métrica '{metric_field}', "
        f"Top {top_n} com gráficos {', '.join(suggested_charts)}."
    )

    return {
        "enabled": True,
        "label_field": label_field,
        "metric_field": metric_field,
        "top_n": top_n,
        "suggested_charts": suggested_charts,
        "explanation": explanation,
    }


def _prompt_guardrail(question: str) -> tuple[bool, str]:
    normalized = _normalize(question)

    if not normalized:
        return True, "Pergunta vazia"

    if len(normalized) > settings.nlp2sql_max_question_length:
        return True, "Pergunta acima do limite permitido"

    for term in BLOCKED_TERMS:
        if term in normalized:
            return True, "Pergunta contém termos proibidos"

    return False, ""


def _sql_guardrail(sql: str) -> tuple[bool, str]:
    normalized = _normalize(sql)

    if not (normalized.startswith("select") or normalized.startswith("with ")):
        return True, "Somente consultas SELECT/CTE são permitidas"

    for pattern in BLOCKED_SQL_PATTERNS:
        if re.search(pattern, normalized):
            return True, "SQL bloqueado por regra de segurança"

    return False, ""


async def _load_schema_catalog(db: AsyncSession) -> dict[str, list[str]]:
    query = text(
        "SELECT table_name, column_name "
        "FROM information_schema.columns "
        "WHERE table_schema = 'public' "
        "ORDER BY table_name, ordinal_position"
    )
    result = await db.execute(query)
    rows = result.mappings().all()

    catalog: dict[str, list[str]] = {}
    for row in rows:
        table_name = str(row.get("table_name", "")).strip()
        column_name = str(row.get("column_name", "")).strip()
        if not table_name or not column_name:
            continue
        catalog.setdefault(table_name, []).append(column_name)

    return catalog


def _find_table_in_question(question: str, schema_catalog: dict[str, list[str]]) -> str | None:
    normalized = _normalize(question)
    for table_name in schema_catalog.keys():
        if table_name.lower() in normalized:
            return table_name
    return None


def _build_select_sql_for_table(table_name: str, schema_catalog: dict[str, list[str]], limit: int = 20) -> str:
    columns = schema_catalog.get(table_name, [])
    safe_columns = [col for col in columns if re.fullmatch(r"[a-zA-Z_][a-zA-Z0-9_]*", col)]
    projection = ", ".join(safe_columns[:12]) if safe_columns else "*"
    safe_table = table_name if re.fullmatch(r"[a-zA-Z_][a-zA-Z0-9_]*", table_name) else ""
    if not safe_table:
        raise HTTPException(status_code=422, detail="Tabela inválida para consulta")
    return f'SELECT {projection} FROM "{safe_table}" LIMIT {limit}'


def _extract_year(normalized_question: str) -> int | None:
    match = re.search(r"\b(20\d{2})\b", normalized_question)
    return int(match.group(1)) if match else None


def _wants_monthly_history(normalized_question: str) -> bool:
    return any(
        token in normalized_question
        for token in [
            "historico por mes",
            "histórico por mes",
            "historico por mês",
            "histórico por mês",
            "por mes",
            "por mês",
            "a cada mes",
            "a cada mês",
            "evolucao mensal",
            "evolução mensal",
        ]
    )


def _reformulation_examples() -> list[str]:
    return [
        "Total de pacientes cadastrados por mês em 2026",
        "Atendimentos realizados por mês em 2026",
        "Top 10 pacientes por quantidade de atendimentos",
        "Liste pacientes de maio de 2026 ordenados por data de nascimento",
    ]


def _build_prompt_feedback(question: str, reason: str) -> str:
    examples = "; ".join(_reformulation_examples())
    return (
        f"{reason}. "
        f"Pergunta recebida: '{question}'. "
        "Dica: informe entidade, período e agregação (ex.: por mês, top N, agrupado por coluna). "
        f"Exemplos válidos: {examples}."
    )


def _fallback_sql_for_unsupported(question: str, schema_catalog: dict[str, list[str]]) -> str:
    normalized = _normalize(question)
    target_year = _extract_year(normalized)

    if "paciente" in normalized and _wants_monthly_history(normalized):
        if target_year is None:
            return (
                "SELECT TO_CHAR(DATE_TRUNC('month', criado_em), 'YYYY-MM') AS mes, COUNT(*) AS total_pacientes "
                "FROM pacientes "
                "GROUP BY DATE_TRUNC('month', criado_em) "
                "ORDER BY DATE_TRUNC('month', criado_em)"
            )
        return (
            "SELECT TO_CHAR(DATE_TRUNC('month', criado_em), 'YYYY-MM') AS mes, COUNT(*) AS total_pacientes "
            "FROM pacientes "
            "WHERE EXTRACT(YEAR FROM criado_em) = :target_year "
            "GROUP BY DATE_TRUNC('month', criado_em) "
            "ORDER BY DATE_TRUNC('month', criado_em)"
        )

    if "atendimento" in normalized or "agendamento" in normalized:
        return (
            "SELECT TO_CHAR(DATE_TRUNC('month', data_hora), 'YYYY-MM') AS mes, COUNT(*) AS total_atendimentos "
            "FROM agendamento "
            "WHERE data_hora <= NOW() "
            "GROUP BY DATE_TRUNC('month', data_hora) "
            "ORDER BY DATE_TRUNC('month', data_hora)"
        )

    table_in_question = _find_table_in_question(normalized, schema_catalog)
    if table_in_question:
        return _build_select_sql_for_table(
            table_in_question,
            schema_catalog,
            limit=min(settings.nlp2sql_max_rows, 20),
        )

    return ""


def _sql_for_question(question: str, schema_catalog: dict[str, list[str]]) -> tuple[str, dict, str]:
    normalized = _normalize(question)

    month_from_name = next((value for key, value in MONTH_MAP.items() if key in normalized), None)
    target_year = _extract_year(normalized)

    if "pacientes" in normalized and "procedimento" in normalized and month_from_name is not None:
        return (
            "SELECT p.nome_procedimento AS procedimento, COUNT(DISTINCT c.paciente_id) AS total "
            "FROM cirurgias c "
            "JOIN procedimentos p ON p.id = c.procedimento_id "
            "WHERE EXTRACT(MONTH FROM c.data_agendada) = :target_month "
            "AND EXTRACT(YEAR FROM c.data_agendada) = EXTRACT(YEAR FROM CURRENT_DATE) "
            "GROUP BY p.nome_procedimento "
            "ORDER BY total DESC "
            "LIMIT 10",
            {"target_month": month_from_name},
            f"Pacientes por procedimento no mês {month_from_name:02d}"
        )

    if "pacientes" in normalized and month_from_name is not None:
        return (
            "SELECT id, nome, telefone, email, TO_CHAR(DATE(criado_em), 'YYYY-MM-DD') AS data_cadastro "
            "FROM pacientes "
            "WHERE EXTRACT(MONTH FROM criado_em) = :target_month "
            "AND EXTRACT(YEAR FROM criado_em) = EXTRACT(YEAR FROM CURRENT_DATE) "
            "ORDER BY criado_em DESC",
            {"target_month": month_from_name},
            f"Pacientes cadastrados no mês {month_from_name:02d}"
        )

    if "pacientes" in normalized and ("hoje" in normalized or "dia" in normalized):
        return (
            "SELECT COUNT(*) AS total_pacientes_hoje "
            "FROM pacientes WHERE DATE(criado_em) = CURRENT_DATE",
            {},
            "Pacientes cadastrados hoje"
        )

    if "pacientes" in normalized and _wants_monthly_history(normalized):
        if target_year is None:
            return (
                "SELECT TO_CHAR(DATE_TRUNC('month', criado_em), 'YYYY-MM') AS mes, COUNT(*) AS total_pacientes "
                "FROM pacientes "
                "GROUP BY DATE_TRUNC('month', criado_em) "
                "ORDER BY DATE_TRUNC('month', criado_em)",
                {},
                "Histórico mensal de pacientes"
            )

        return (
            "SELECT TO_CHAR(DATE_TRUNC('month', criado_em), 'YYYY-MM') AS mes, COUNT(*) AS total_pacientes "
            "FROM pacientes "
            "WHERE EXTRACT(YEAR FROM criado_em) = :target_year "
            "GROUP BY DATE_TRUNC('month', criado_em) "
            "ORDER BY DATE_TRUNC('month', criado_em)",
            {"target_year": target_year},
            f"Histórico mensal de pacientes em {target_year}"
        )

    if "pacientes" in normalized and ("mes" in normalized or "mês" in normalized or "novos" in normalized):
        return (
            "SELECT COUNT(*) AS total_pacientes_mes "
            "FROM pacientes "
            "WHERE DATE_TRUNC('month', criado_em) = DATE_TRUNC('month', CURRENT_DATE)",
            {},
            "Pacientes cadastrados no mês atual"
        )

    if ("atendimento" in normalized or "atendimentos" in normalized or "agendamento" in normalized) and month_from_name is not None:
        params = {"target_month": month_from_name}
        year_sql = "EXTRACT(YEAR FROM CURRENT_DATE)"
        if target_year is not None:
            params["target_year"] = target_year
            year_sql = ":target_year"

        return (
            "SELECT TO_CHAR(DATE(data_hora), 'YYYY-MM-DD') AS dia, COUNT(*) AS total_atendimentos "
            "FROM agendamento "
            "WHERE EXTRACT(MONTH FROM data_hora) = :target_month "
            f"AND EXTRACT(YEAR FROM data_hora) = {year_sql} "
            "AND data_hora <= NOW() "
            "GROUP BY DATE(data_hora) "
            "ORDER BY DATE(data_hora)",
            params,
            f"Atendimentos realizados no mês {month_from_name:02d}"
        )

    if ("atendimento" in normalized or "atendimentos" in normalized or "agendamento" in normalized) and _wants_monthly_history(normalized):
        if target_year is None:
            return (
                "SELECT TO_CHAR(DATE_TRUNC('month', data_hora), 'YYYY-MM') AS mes, COUNT(*) AS total_atendimentos "
                "FROM agendamento "
                "WHERE data_hora <= NOW() "
                "GROUP BY DATE_TRUNC('month', data_hora) "
                "ORDER BY DATE_TRUNC('month', data_hora)",
                {},
                "Histórico mensal de atendimentos realizados"
            )

        return (
            "SELECT TO_CHAR(DATE_TRUNC('month', data_hora), 'YYYY-MM') AS mes, COUNT(*) AS total_atendimentos "
            "FROM agendamento "
            "WHERE data_hora <= NOW() "
            "AND EXTRACT(YEAR FROM data_hora) = :target_year "
            "GROUP BY DATE_TRUNC('month', data_hora) "
            "ORDER BY DATE_TRUNC('month', data_hora)",
            {"target_year": target_year},
            f"Histórico mensal de atendimentos realizados em {target_year}"
        )

    if "agendamento" in normalized and ("semana" in normalized or "ultimos" in normalized or "últimos" in normalized):
        return (
            "SELECT TO_CHAR(DATE(data_hora), 'YYYY-MM-DD') AS dia, COUNT(*) AS total "
            "FROM agendamento "
            "WHERE data_hora >= CURRENT_DATE - INTERVAL '6 day' "
            "GROUP BY DATE(data_hora) "
            "ORDER BY DATE(data_hora)",
            {},
            "Agendamentos por dia nos últimos 7 dias"
        )

    if "procedimento" in normalized and ("top" in normalized or "mais" in normalized):
        return (
            "SELECT p.nome_procedimento AS procedimento, COUNT(*) AS total "
            "FROM cirurgias c "
            "JOIN procedimentos p ON p.id = c.procedimento_id "
            "GROUP BY p.nome_procedimento "
            "ORDER BY total DESC "
            "LIMIT 5",
            {},
            "Top 5 procedimentos por volume"
        )

    if "cirurgia" in normalized and ("mes" in normalized or "mês" in normalized):
        return (
            "SELECT COUNT(*) AS total_cirurgias_mes "
            "FROM cirurgias "
            "WHERE DATE_TRUNC('month', data_agendada) = DATE_TRUNC('month', CURRENT_DATE)",
            {},
            "Cirurgias agendadas no mês"
        )

    if ("tabelas" in normalized or "schema" in normalized or "esquema" in normalized) and (
        "listar" in normalized or "mostrar" in normalized or "quais" in normalized
    ):
        return (
            "SELECT table_name FROM information_schema.tables "
            "WHERE table_schema = 'public' "
            "ORDER BY table_name",
            {},
            "Tabelas disponíveis no schema público"
        )

    table_in_question = _find_table_in_question(normalized, schema_catalog)
    if table_in_question and ("coluna" in normalized or "colunas" in normalized):
        return (
            "SELECT column_name FROM information_schema.columns "
            "WHERE table_schema = 'public' "
            "AND table_name = :table_name "
            "ORDER BY ordinal_position",
            {"table_name": table_in_question},
            f"Colunas da tabela {table_in_question}"
        )

    if table_in_question and ("listar" in normalized or "mostrar" in normalized or "todos" in normalized):
        return (
            _build_select_sql_for_table(table_in_question, schema_catalog, limit=min(settings.nlp2sql_max_rows, 30)),
            {},
            f"Prévia da tabela {table_in_question}"
        )

    raise HTTPException(status_code=422, detail="Pergunta não suportada ainda")

@router.get("/dashboard")
async def get_dashboard_analytics():
    """Retorna métricas e dados de gráficos para o dashboard de analytics."""
    return {
        "metrics": {
            "total_revenue": 0,
            "total_customers": 0,
            "conversion_rate": 0,
            "average_ticket": 0
        },
        "charts": {
            "revenue_by_month": [],
            "customers_by_status": [],
            "top_products": []
        }
    }

@router.get("/predictions")
async def get_predictions():
    """Retorna predições de ML para churn, LTV e probabilidade de próxima compra."""
    return {
        "churn_risk": [],
        "lifetime_value": [],
        "next_purchase_probability": []
    }

@router.get("/trends")
async def get_trends():
    """Retorna análise de tendências de crescimento, receita e produtos."""
    return {
        "customer_growth": [],
        "revenue_trends": [],
        "product_trends": []
    }


@router.get("/schema-catalog")
async def get_schema_catalog(db: AsyncSession = Depends(get_db)):
    catalog = await _load_schema_catalog(db)
    return {
        "success": True,
        "tables": list(catalog.keys()),
        "columns_by_table": catalog,
    }


@router.post("/nlp2sql")
async def nlp2sql_query(payload: Nlp2SqlRequest, db: AsyncSession = Depends(get_db)):
    if not settings.nlp2sql_enabled:
        return {
            "success": False,
            "answer": "Módulo NLP2SQL desativado.",
            "generated_sql": "",
            "columns": [],
            "rows": [],
            "row_count": 0,
            "source": "disabled",
            "guardrail": {
                "blocked": True,
                "reason": "NLP2SQL_ENABLED=false"
            }
        }

    blocked, reason = _prompt_guardrail(payload.question)
    if blocked:
        return {
            "success": False,
            "answer": _build_prompt_feedback(payload.question, "Consulta bloqueada pelo guardrail de entrada"),
            "generated_sql": "",
            "columns": [],
            "rows": [],
            "row_count": 0,
            "source": "guardrail",
            "guardrail": {
                "blocked": True,
                "reason": reason
            }
        }

    schema_catalog = await _load_schema_catalog(db)
    normalized_question = _normalize(payload.question)

    if ("tabelas" in normalized_question or "schema" in normalized_question or "esquema" in normalized_question) and (
        "listar" in normalized_question or "mostrar" in normalized_question or "quais" in normalized_question
    ):
        table_rows = [{"table_name": table_name} for table_name in sorted(schema_catalog.keys())]
        return {
            "success": True,
            "answer": f"Catálogo de schema carregado. Retornamos {len(table_rows)} tabela(s).",
            "generated_sql": "",
            "columns": ["table_name"],
            "rows": table_rows,
            "row_count": len(table_rows),
            "source": "schema-catalog",
            "insights_plan": {
                "enabled": False,
                "reason": "Listagem de metadados"
            },
            "schema_catalog_summary": {
                "table_count": len(schema_catalog),
                "tables": list(schema_catalog.keys())[:25]
            },
            "guardrail": {
                "blocked": False,
                "reason": ""
            }
        }

    table_in_question = _find_table_in_question(normalized_question, schema_catalog)
    if table_in_question and ("coluna" in normalized_question or "colunas" in normalized_question):
        columns_rows = [{"column_name": col} for col in schema_catalog.get(table_in_question, [])]
        return {
            "success": True,
            "answer": f"Colunas da tabela {table_in_question}. Retornamos {len(columns_rows)} coluna(s).",
            "generated_sql": "",
            "columns": ["column_name"],
            "rows": columns_rows,
            "row_count": len(columns_rows),
            "source": "schema-catalog",
            "insights_plan": {
                "enabled": False,
                "reason": "Listagem de metadados"
            },
            "schema_catalog_summary": {
                "table_count": len(schema_catalog),
                "tables": list(schema_catalog.keys())[:25]
            },
            "guardrail": {
                "blocked": False,
                "reason": ""
            }
        }

    try:
        sql, params, description = _sql_for_question(payload.question, schema_catalog)
    except HTTPException as ex:
        if ex.status_code == 422:
            fallback_sql = _fallback_sql_for_unsupported(payload.question, schema_catalog)
            return {
                "success": False,
                "answer": _build_prompt_feedback(
                    payload.question,
                    "Pergunta inválida para NLP2SQL (422)"
                ),
                "generated_sql": fallback_sql,
                "columns": [],
                "rows": [],
                "row_count": 0,
                "source": "guardrail",
                "guardrail": {
                    "blocked": True,
                    "reason": "Pergunta inválida para NLP2SQL (422)"
                }
            }
        raise

    sql_blocked, sql_reason = _sql_guardrail(sql)
    if sql_blocked:
        return {
            "success": False,
            "answer": "Consulta bloqueada pelo guardrail SQL.",
            "generated_sql": sql,
            "columns": [],
            "rows": [],
            "row_count": 0,
            "source": "guardrail",
            "guardrail": {
                "blocked": True,
                "reason": sql_reason
            }
        }

    result = await db.execute(text(sql), params)
    mappings = result.mappings().all()
    limited_rows = [dict(row) for row in mappings[: settings.nlp2sql_max_rows]]
    columns = list(result.keys())

    return {
        "success": True,
        "answer": f"{description}. Retornamos {len(limited_rows)} linha(s).",
        "generated_sql": sql,
        "columns": columns,
        "rows": limited_rows,
        "row_count": len(limited_rows),
        "source": "rule-based",
        "insights_plan": _build_insights_plan(payload.question, columns, limited_rows),
        "schema_catalog_summary": {
            "table_count": len(schema_catalog),
            "tables": list(schema_catalog.keys())[:25]
        },
        "guardrail": {
            "blocked": False,
            "reason": ""
        }
    }
