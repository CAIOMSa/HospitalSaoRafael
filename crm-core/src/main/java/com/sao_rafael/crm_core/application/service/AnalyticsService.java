package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.adapter.web.dto.*;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.CirurgiaEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.ConversaEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.FuncionarioEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.JornadaPacienteEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.PacienteEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private final CirurgiaJpaRepository cirurgiaRepository;
    private final FuncionarioJpaRepository funcionarioRepository;
    private final PacienteJpaRepository pacienteRepository;
    private final JornadaPacienteJpaRepository jornadaRepository;
    private final ConversaJpaRepository conversaRepository;

    private static final BigDecimal SALARIO_BASE = new BigDecimal("4200");

    public AnalyticsService(
            CirurgiaJpaRepository cirurgiaRepository,
            FuncionarioJpaRepository funcionarioRepository,
            PacienteJpaRepository pacienteRepository,
            JornadaPacienteJpaRepository jornadaRepository,
            ConversaJpaRepository conversaRepository
    ) {
        this.cirurgiaRepository = cirurgiaRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.pacienteRepository = pacienteRepository;
        this.jornadaRepository = jornadaRepository;
        this.conversaRepository = conversaRepository;
    }

    public FinanceAnalyticsResponseDto getFinanceAnalytics(LocalDate from, LocalDate to) {
        LocalDateTime start = toStartOfDay(from);
        LocalDateTime end = toEndOfDay(to);

        List<CirurgiaEntity> cirurgias = cirurgiaRepository.findAll().stream()
                .filter(item -> inRange(resolveCirurgiaDate(item), start, end))
                .toList();

        Map<String, FinanceAccumulator> grouped = new LinkedHashMap<>();
        long realizadas = 0;
        long agendadas = 0;
        BigDecimal totalReceita = BigDecimal.ZERO;
        BigDecimal totalCusto = BigDecimal.ZERO;

        for (CirurgiaEntity cirurgia : cirurgias) {
            String procedimento = cirurgia.getProcedimento() != null && cirurgia.getProcedimento().getNomeProcedimento() != null
                    ? cirurgia.getProcedimento().getNomeProcedimento()
                    : "Sem procedimento";

            FinanceAccumulator current = grouped.computeIfAbsent(procedimento, key -> new FinanceAccumulator());

            BigDecimal receita = estimateRevenue(cirurgia.getRisco());
            BigDecimal custo = estimateCost(cirurgia.getRisco());

            if (cirurgia.getDataRealizada() != null) {
                realizadas += 1;
                current.realizadas += 1;
            } else {
                agendadas += 1;
                current.agendadas += 1;
            }

            current.receita = current.receita.add(receita);
            current.custo = current.custo.add(custo);
            totalReceita = totalReceita.add(receita);
            totalCusto = totalCusto.add(custo);
        }

        List<FinanceAnalyticsItemDto> procedimentos = grouped.entrySet().stream()
                .map(entry -> {
                    FinanceAccumulator value = entry.getValue();
                    return new FinanceAnalyticsItemDto(
                            entry.getKey(),
                            value.realizadas,
                            value.agendadas,
                            value.receita,
                            value.custo,
                            value.receita.subtract(value.custo)
                    );
                })
                .sorted(Comparator.comparing(FinanceAnalyticsItemDto::realizadas).reversed())
                .toList();

        List<FinancePayrollItemDto> folha = funcionarioRepository.findAll().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAtivo()))
                .filter(item -> inRange(item.getCriadoEm(), start, end) || (start == null && end == null))
                .map(item -> new FinancePayrollItemDto(
                        item.getId(),
                        item.getNome(),
                        item.getCargo() != null ? item.getCargo().getCargo() : "Sem cargo",
                        item.getCargo() != null ? item.getCargo().getDepartamento() : "-",
                        estimateSalary(item)
                ))
                .toList();

        BigDecimal folhaTotal = folha.stream()
                .map(FinancePayrollItemDto::salarioEstimado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new FinanceAnalyticsResponseDto(
                realizadas,
                agendadas,
                totalReceita,
                totalCusto,
                totalReceita.subtract(totalCusto),
                folhaTotal,
                procedimentos,
                folha
        );
    }

    public LeadsAnalyticsResponseDto getLeadsAnalytics(LocalDate from, LocalDate to) {
        LocalDateTime start = toStartOfDay(from);
        LocalDateTime end = toEndOfDay(to);

        List<PacienteEntity> patients = pacienteRepository.findAll().stream()
                .filter(item -> inRange(item.getCriadoEm(), start, end))
                .toList();

        Map<Long, JornadaPacienteEntity> latestJourneyByPatient = new HashMap<>();
        for (JornadaPacienteEntity journey : jornadaRepository.findAll()) {
            if (journey.getPaciente() == null || journey.getPaciente().getId() == null) {
                continue;
            }

            if (!inRange(journey.getDataInicio(), start, end)) {
                continue;
            }

            Long patientId = journey.getPaciente().getId();
            JornadaPacienteEntity current = latestJourneyByPatient.get(patientId);
            if (current == null || isAfter(journey.getDataInicio(), current.getDataInicio())) {
                latestJourneyByPatient.put(patientId, journey);
            }
        }

        Set<Long> conversationPatients = new HashSet<>();
        for (ConversaEntity conversation : conversaRepository.findAll()) {
            if (conversation.getContato() == null || conversation.getContato().getPaciente() == null) {
                continue;
            }

            if (!inRange(conversation.getAtualizadoEm(), start, end)) {
                continue;
            }

            conversationPatients.add(conversation.getContato().getPaciente().getId());
        }

        List<LeadAnalyticsItemDto> leads = new ArrayList<>();
        for (PacienteEntity patient : patients) {
            long patientId = patient.getId();
            JornadaPacienteEntity journey = latestJourneyByPatient.get(patientId);
            boolean hasConversation = conversationPatients.contains(patientId);

            LeadStage stage = resolveStage(hasConversation, journey != null ? journey.getEtapa() : null);
            BigDecimal potential = stage.valorBase
                    .add(hasConversation ? new BigDecimal("1500") : BigDecimal.ZERO)
                    .add(journey != null ? new BigDecimal("2200") : BigDecimal.ZERO);
            BigDecimal weighted = potential.multiply(stage.probabilidade).setScale(0, RoundingMode.HALF_UP);

            leads.add(new LeadAnalyticsItemDto(
                    patient.getId(),
                    patient.getNome(),
                    patient.getTelefone(),
                    patient.getEmail(),
                    hasConversation ? "WhatsApp" : "Site/Recepcao",
                    stage.label,
                    stage.probabilidade,
                    potential,
                    weighted
            ));
        }

        leads.sort(Comparator.comparing(LeadAnalyticsItemDto::valorPonderado).reversed());

        long novos = leads.stream().filter(item -> "Novo".equals(item.etapa())).count();
        long contato = leads.stream().filter(item -> "Contato".equals(item.etapa())).count();
        long triagem = leads.stream().filter(item -> "Triagem".equals(item.etapa())).count();
        long qualificados = leads.stream().filter(item -> "Qualificado".equals(item.etapa())).count();

        BigDecimal pipelineBruto = leads.stream()
                .map(LeadAnalyticsItemDto::valorPotencial)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pipelinePonderado = leads.stream()
                .map(LeadAnalyticsItemDto::valorPonderado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new LeadsAnalyticsResponseDto(
                (long) leads.size(),
                novos,
                contato,
                triagem,
                qualificados,
                pipelineBruto,
                pipelinePonderado,
                leads
        );
    }

    private LocalDateTime resolveCirurgiaDate(CirurgiaEntity cirurgia) {
        if (cirurgia.getDataRealizada() != null) {
            return cirurgia.getDataRealizada();
        }
        return cirurgia.getDataAgendada();
    }

    private BigDecimal estimateRevenue(CirurgiaEntity.RiscoCirurgia risk) {
        if (risk == null) {
            return new BigDecimal("15000");
        }

        return switch (risk) {
            case BAIXO -> new BigDecimal("12000");
            case MEDIO -> new BigDecimal("18000");
            case ALTO -> new BigDecimal("26000");
            case CRITICO -> new BigDecimal("38000");
        };
    }

    private BigDecimal estimateCost(CirurgiaEntity.RiscoCirurgia risk) {
        if (risk == null) {
            return new BigDecimal("9500");
        }

        return switch (risk) {
            case BAIXO -> new BigDecimal("7000");
            case MEDIO -> new BigDecimal("12000");
            case ALTO -> new BigDecimal("19000");
            case CRITICO -> new BigDecimal("30000");
        };
    }

    private BigDecimal estimateSalary(FuncionarioEntity employee) {
        String role = normalize(employee.getCargo() != null ? employee.getCargo().getCargo() : null);
        String level = normalize(employee.getCargo() != null ? employee.getCargo().getNivelHierarquico() : null);

        BigDecimal base = SALARIO_BASE;

        if (role.contains("cirurg") || role.contains("medic")) {
            base = new BigDecimal("21500");
        } else if (role.contains("enferme")) {
            base = new BigDecimal("7600");
        } else if (role.contains("tecn")) {
            base = new BigDecimal("4600");
        } else if (role.contains("admin") || role.contains("analist")) {
            base = new BigDecimal("5100");
        }

        if (level.contains("senior")) {
            return base.multiply(new BigDecimal("1.2")).setScale(0, RoundingMode.HALF_UP);
        }

        if (level.contains("junior")) {
            return base.multiply(new BigDecimal("0.82")).setScale(0, RoundingMode.HALF_UP);
        }

        return base;
    }

    private LeadStage resolveStage(boolean hasConversation, Object etapaObject) {
        String etapa = normalize(etapaObject != null ? etapaObject.toString() : null);

        if (hasConversation && !etapa.isBlank()) {
            return LeadStage.QUALIFICADO;
        }

        if (!etapa.isBlank()) {
            return LeadStage.TRIAGEM;
        }

        if (hasConversation) {
            return LeadStage.CONTATO;
        }

        return LeadStage.NOVO;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value
                .toLowerCase(Locale.ROOT)
                .replace("á", "a")
                .replace("ã", "a")
                .replace("â", "a")
                .replace("é", "e")
                .replace("ê", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ô", "o")
                .replace("õ", "o")
                .replace("ú", "u")
                .replace("ç", "c");
    }

    private boolean inRange(LocalDateTime value, LocalDateTime start, LocalDateTime end) {
        if (value == null) {
            return start == null && end == null;
        }

        if (start != null && value.isBefore(start)) {
            return false;
        }

        if (end != null && value.isAfter(end)) {
            return false;
        }

        return true;
    }

    private boolean isAfter(LocalDateTime left, LocalDateTime right) {
        if (left == null) {
            return false;
        }
        if (right == null) {
            return true;
        }
        return left.isAfter(right);
    }

    private LocalDateTime toStartOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay();
    }

    private LocalDateTime toEndOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atTime(LocalTime.MAX);
    }

    private enum LeadStage {
        NOVO("Novo", new BigDecimal("0.20"), new BigDecimal("3000")),
        CONTATO("Contato", new BigDecimal("0.35"), new BigDecimal("6000")),
        TRIAGEM("Triagem", new BigDecimal("0.50"), new BigDecimal("9000")),
        QUALIFICADO("Qualificado", new BigDecimal("0.68"), new BigDecimal("13000"));

        final String label;
        final BigDecimal probabilidade;
        final BigDecimal valorBase;

        LeadStage(String label, BigDecimal probabilidade, BigDecimal valorBase) {
            this.label = label;
            this.probabilidade = probabilidade;
            this.valorBase = valorBase;
        }
    }

    private static class FinanceAccumulator {
        long realizadas = 0;
        long agendadas = 0;
        BigDecimal receita = BigDecimal.ZERO;
        BigDecimal custo = BigDecimal.ZERO;
    }
}
