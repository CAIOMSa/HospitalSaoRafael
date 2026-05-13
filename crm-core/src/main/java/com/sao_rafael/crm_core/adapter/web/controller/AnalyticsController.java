package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.adapter.web.dto.FinanceAnalyticsResponseDto;
import com.sao_rafael.crm_core.adapter.web.dto.LeadAnalyticsItemDto;
import com.sao_rafael.crm_core.adapter.web.dto.LeadsAnalyticsResponseDto;
import com.sao_rafael.crm_core.application.service.AnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/financeiro")
    public ResponseEntity<FinanceAnalyticsResponseDto> getFinancialAnalytics(
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(analyticsService.getFinanceAnalytics(from, to));
    }

    @GetMapping("/leads")
    public ResponseEntity<LeadsAnalyticsResponseDto> getLeadsAnalytics(
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(name = "canal", required = false) String canal,
            @RequestParam(name = "origem", required = false) String origem,
            @RequestParam(name = "responsavel", required = false) String responsavel,
            @RequestParam(name = "etapa", required = false) String etapa,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "cidade", required = false) String cidade,
            @RequestParam(name = "tag", required = false) String tag,
            @RequestParam(name = "scoreMin", required = false) Integer scoreMin,
            @RequestParam(name = "scoreMax", required = false) Integer scoreMax,
            @RequestParam(name = "valorMin", required = false) java.math.BigDecimal valorMin,
            @RequestParam(name = "valorMax", required = false) java.math.BigDecimal valorMax
    ) {
        return ResponseEntity.ok(
            analyticsService.getLeadsAnalytics(
                from,
                to,
                canal,
                origem,
                responsavel,
                etapa,
                status,
                cidade,
                tag,
                scoreMin,
                scoreMax,
                valorMin,
                valorMax
            )
        );
    }

    @GetMapping(value = "/leads/csv", produces = "text/csv")
    public ResponseEntity<byte[]> getLeadsCsv(
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(name = "canal", required = false) String canal,
            @RequestParam(name = "origem", required = false) String origem,
            @RequestParam(name = "responsavel", required = false) String responsavel,
            @RequestParam(name = "etapa", required = false) String etapa,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "cidade", required = false) String cidade,
            @RequestParam(name = "tag", required = false) String tag,
            @RequestParam(name = "scoreMin", required = false) Integer scoreMin,
            @RequestParam(name = "scoreMax", required = false) Integer scoreMax,
            @RequestParam(name = "valorMin", required = false) BigDecimal valorMin,
            @RequestParam(name = "valorMax", required = false) BigDecimal valorMax
    ) {
        LeadsAnalyticsResponseDto response = analyticsService.getLeadsAnalytics(
                from,
                to,
                canal,
                origem,
                responsavel,
                etapa,
                status,
                cidade,
                tag,
                scoreMin,
                scoreMax,
                valorMin,
                valorMax
        );

        StringBuilder csv = new StringBuilder();
        csv.append("pacienteId,nome,telefone,email,canal,origem,responsavel,etapa,status,probabilidade,score,valorPotencial,valorPonderado,proximaAcao,proximaAcaoDataHora,slaAtrasado,diasSemInteracao,mensagens,cidade,tags\n");

        List<LeadAnalyticsItemDto> leads = response.leads() != null ? response.leads() : List.of();
        for (LeadAnalyticsItemDto lead : leads) {
            csv.append(csvValue(lead.pacienteId())).append(',')
                    .append(csvValue(lead.nome())).append(',')
                    .append(csvValue(lead.telefone())).append(',')
                    .append(csvValue(lead.email())).append(',')
                    .append(csvValue(lead.canal())).append(',')
                    .append(csvValue(lead.origem())).append(',')
                    .append(csvValue(lead.responsavel())).append(',')
                    .append(csvValue(lead.etapa())).append(',')
                    .append(csvValue(lead.status())).append(',')
                    .append(csvValue(lead.probabilidade())).append(',')
                    .append(csvValue(lead.score())).append(',')
                    .append(csvValue(lead.valorPotencial())).append(',')
                    .append(csvValue(lead.valorPonderado())).append(',')
                    .append(csvValue(lead.proximaAcao())).append(',')
                    .append(csvValue(lead.proximaAcaoDataHora())).append(',')
                    .append(csvValue(lead.slaAtrasado())).append(',')
                    .append(csvValue(lead.diasSemInteracao())).append(',')
                    .append(csvValue(lead.mensagens())).append(',')
                    .append(csvValue(lead.cidade())).append(',')
                    .append(csvValue(lead.tags() == null ? "" : String.join("|", lead.tags())))
                    .append('\n');
        }

        String fileName = "leads-" + rangeSuffix(from, to) + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping(value = "/financeiro/csv", produces = "text/csv")
    public ResponseEntity<byte[]> getFinanceiroCsv(
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        FinanceAnalyticsResponseDto response = analyticsService.getFinanceAnalytics(from, to);

        StringBuilder csv = new StringBuilder();
        csv.append("procedimento,realizadas,agendadas,receitaEstimada,custoEstimado,margemEstimada\n");

        if (response.procedimentos() != null) {
            response.procedimentos().forEach(item -> csv.append(csvValue(item.procedimento())).append(',')
                    .append(csvValue(item.realizadas())).append(',')
                    .append(csvValue(item.agendadas())).append(',')
                    .append(csvValue(item.receitaEstimada())).append(',')
                    .append(csvValue(item.custoEstimado())).append(',')
                    .append(csvValue(item.margemEstimada())).append('\n'));
        }

        csv.append("\nfolha_funcionarioId,funcionario,cargo,departamento,salarioEstimado\n");
        if (response.folha() != null) {
            response.folha().forEach(item -> csv.append(csvValue(item.funcionarioId())).append(',')
                    .append(csvValue(item.funcionario())).append(',')
                    .append(csvValue(item.cargo())).append(',')
                    .append(csvValue(item.departamento())).append(',')
                    .append(csvValue(item.salarioEstimado())).append('\n'));
        }

        String fileName = "financeiro-" + rangeSuffix(from, to) + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static String rangeSuffix(LocalDate from, LocalDate to) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        String fromValue = from == null ? "inicio" : formatter.format(from);
        String toValue = to == null ? "fim" : formatter.format(to);
        return fromValue + "-" + toValue;
    }

    private static String csvValue(Object value) {
        if (value == null) {
            return "\"\"";
        }

        String text = String.valueOf(value)
                .replace("\"", "\"\"")
                .replace("\r", " ")
                .replace("\n", " ");

        return "\"" + text + "\"";
    }
}
