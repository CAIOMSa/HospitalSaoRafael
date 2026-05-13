package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.adapter.web.dto.*;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.AgendamentoEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.CirurgiaEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.ConversaEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.EnderecoEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.FuncionarioEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.JornadaPacienteEntity;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.MensagemEntity;
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
    private final MensagemJpaRepository mensagemRepository;
    private final AgendamentoJpaRepository agendamentoRepository;

    private static final BigDecimal SALARIO_BASE = new BigDecimal("4200");

    public AnalyticsService(
            CirurgiaJpaRepository cirurgiaRepository,
            FuncionarioJpaRepository funcionarioRepository,
            PacienteJpaRepository pacienteRepository,
            JornadaPacienteJpaRepository jornadaRepository,
            ConversaJpaRepository conversaRepository,
            MensagemJpaRepository mensagemRepository,
            AgendamentoJpaRepository agendamentoRepository
    ) {
        this.cirurgiaRepository = cirurgiaRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.pacienteRepository = pacienteRepository;
        this.jornadaRepository = jornadaRepository;
        this.conversaRepository = conversaRepository;
        this.mensagemRepository = mensagemRepository;
        this.agendamentoRepository = agendamentoRepository;
    }

    private boolean matchesFilters(
            LeadAnalyticsItemDto lead,
            String canal,
            String origem,
            String responsavel,
            String etapa,
            String status,
            String cidade,
            String tag,
            Integer scoreMin,
            Integer scoreMax,
            BigDecimal valorMin,
            BigDecimal valorMax
    ) {
        if (!matchesText(lead.canal(), canal)) {
            return false;
        }
        if (!matchesText(lead.origem(), origem)) {
            return false;
        }
        if (!matchesText(lead.responsavel(), responsavel)) {
            return false;
        }
        if (!matchesText(lead.etapa(), etapa)) {
            return false;
        }
        if (!matchesText(lead.status(), status)) {
            return false;
        }
        if (!matchesText(lead.cidade(), cidade)) {
            return false;
        }

        if (tag != null && !tag.isBlank()) {
            String normalizedTag = normalize(tag);
            boolean hasTag = lead.tags() != null && lead.tags().stream().map(this::normalize).anyMatch(item -> item.contains(normalizedTag));
            if (!hasTag) {
                return false;
            }
        }

        int score = lead.score() != null ? lead.score() : 0;
        if (scoreMin != null && score < scoreMin) {
            return false;
        }
        if (scoreMax != null && score > scoreMax) {
            return false;
        }

        BigDecimal potential = lead.valorPotencial() != null ? lead.valorPotencial() : BigDecimal.ZERO;
        if (valorMin != null && potential.compareTo(valorMin) < 0) {
            return false;
        }
        if (valorMax != null && potential.compareTo(valorMax) > 0) {
            return false;
        }

        return true;
    }

    private boolean matchesText(String value, String filter) {
        if (filter == null || filter.isBlank()) {
            return true;
        }
        return normalize(value).contains(normalize(filter));
    }

    private Integer calculateLeadScore(
            LeadStage stage,
            boolean hasConversation,
            boolean hasFutureSchedule,
            int messageCount,
            LocalDateTime createdAt,
            LocalDateTime now
    ) {
        int score = 20;
        score += Math.min(25, messageCount * 4);
        score += hasConversation ? 15 : 0;
        score += hasFutureSchedule ? 20 : 0;
        score += switch (stage) {
            case QUALIFICADO -> 12;
            case TRIAGEM -> 8;
            case CONTATO -> 5;
            default -> 0;
        };

        if (createdAt != null && java.time.Duration.between(createdAt, now).toDays() <= 7) {
            score += 8;
        }

        return Math.min(100, score);
    }

    private String resolveLeadOrigin(PacienteEntity patient, boolean hasConversation) {
        if (hasConversation) {
            return "WhatsApp";
        }

        String email = normalize(patient.getEmail());
        if (email.contains("gmail") || email.contains("hotmail")) {
            return "Meta Ads";
        }
        if (email.contains("yahoo") || email.contains("outlook")) {
            return "Google";
        }

        String[] origins = {"Indicacao", "Organico", "Landing page", "Site"};
        return origins[Math.toIntExact(Math.abs(patient.getId() % origins.length))];
    }

    private String resolveLeadOwner(List<MensagemEntity> messages, AgendamentoEntity nextSchedule, Long patientId) {
        if (messages != null && !messages.isEmpty()) {
            MensagemEntity latest = messages.get(messages.size() - 1);
            if (latest.getEnviadoPor() != null
                    && latest.getEnviadoPor().getFuncionario() != null
                    && latest.getEnviadoPor().getFuncionario().getNome() != null) {
                return latest.getEnviadoPor().getFuncionario().getNome();
            }
        }

        if (nextSchedule != null && nextSchedule.getMedico() != null && nextSchedule.getMedico().getNome() != null) {
            return nextSchedule.getMedico().getNome();
        }

        String[] owners = {"Caio", "Fernanda", "Rafael", "Ana"};
        return owners[Math.toIntExact(Math.abs(patientId % owners.length))];
    }

    private NextAction resolveNextAction(LeadStage stage, boolean hasConversation, AgendamentoEntity nextSchedule, LocalDateTime now) {
        if (!hasConversation) {
            return new NextAction("Retornar WhatsApp", now.plusHours(4));
        }

        if (nextSchedule != null && nextSchedule.getData_hora() != null) {
            return new NextAction("Confirmar consulta", nextSchedule.getData_hora().minusHours(1));
        }

        if (stage == LeadStage.QUALIFICADO || stage == LeadStage.TRIAGEM) {
            return new NextAction("Agendar consulta", now.plusDays(1));
        }

        return new NextAction("Follow-up comercial", now.plusDays(2));
    }

    private LocalDateTime resolveLatestInteraction(
            PacienteEntity patient,
            List<JornadaPacienteEntity> journeys,
            List<ConversaEntity> conversations,
            List<MensagemEntity> messages,
            List<AgendamentoEntity> schedules
    ) {
        LocalDateTime latest = patient.getAtualizadoEm() != null ? patient.getAtualizadoEm() : patient.getCriadoEm();

        for (JornadaPacienteEntity journey : journeys) {
            latest = maxDateTime(latest, journey.getDataInicio());
            latest = maxDateTime(latest, journey.getDataFim());
        }
        for (ConversaEntity conversation : conversations) {
            latest = maxDateTime(latest, conversation.getAtualizadoEm());
            latest = maxDateTime(latest, conversation.getIniciadoEm());
        }
        for (MensagemEntity message : messages) {
            latest = maxDateTime(latest, message.getDataEnvio());
        }
        for (AgendamentoEntity schedule : schedules) {
            latest = maxDateTime(latest, schedule.getData_hora());
        }

        return latest;
    }

    private List<String> resolveTags(
            LeadStage stage,
            boolean hasConversation,
            boolean hasFutureSchedule,
            BigDecimal weighted,
            String status
    ) {
        List<String> tags = new ArrayList<>();
        tags.add("etapa-" + normalize(stage.label));
        tags.add("status-" + normalize(status));

        if (hasConversation) {
            tags.add("whatsapp");
        } else {
            tags.add("sem-resposta");
        }

        if (!hasFutureSchedule) {
            tags.add("sem-follow-up");
        }

        if (weighted.compareTo(new BigDecimal("10000")) >= 0) {
            tags.add("alto-valor");
        }

        return tags;
    }

    private List<LeadAlertDto> resolveAlerts(
            NextAction nextAction,
            boolean hasConversation,
            int score,
            boolean hasFutureSchedule,
            long daysWithoutInteraction
    ) {
        List<LeadAlertDto> alerts = new ArrayList<>();

        if (nextAction.dataHora() != null && nextAction.dataHora().isBefore(LocalDateTime.now())) {
            alerts.add(new LeadAlertDto("SLA_VENCIDO", "ALTA", "SLA da proxima acao vencido."));
        }

        if (daysWithoutInteraction >= 7) {
            alerts.add(new LeadAlertDto("LEAD_PARADO", "MEDIA", "Lead sem interacao ha 7 dias ou mais."));
        }

        if (!hasConversation) {
            alerts.add(new LeadAlertDto("SEM_RESPOSTA", "MEDIA", "Lead sem conversa ativa no WhatsApp."));
        }

        if (score >= 85 && !hasFutureSchedule) {
            alerts.add(new LeadAlertDto("SEM_FOLLOW_UP", "ALTA", "Lead quente sem follow-up agendado."));
        }

        return alerts;
    }

    private List<LeadTimelineEventDto> buildTimeline(
            PacienteEntity patient,
            List<JornadaPacienteEntity> journeys,
            List<ConversaEntity> conversations,
            List<MensagemEntity> messages,
            List<AgendamentoEntity> schedules
    ) {
        List<LeadTimelineEventDto> events = new ArrayList<>();

        events.add(new LeadTimelineEventDto(
                patient.getCriadoEm(),
                "Cadastro",
                "Lead criado",
                patient.getEmail()
        ));

        for (JornadaPacienteEntity journey : journeys) {
            events.add(new LeadTimelineEventDto(
                    journey.getDataInicio(),
                    "Etapa",
                    "Mudanca de etapa",
                    journey.getEtapa() != null ? journey.getEtapa().getNome() : "Etapa nao definida"
            ));
        }

        for (ConversaEntity conversation : conversations) {
            events.add(new LeadTimelineEventDto(
                    conversation.getIniciadoEm(),
                    "WhatsApp",
                    "Conversa iniciada",
                    conversation.getContato() != null ? conversation.getContato().getNumero() : ""
            ));
        }

        for (MensagemEntity message : messages.stream().skip(Math.max(0, messages.size() - 8)).toList()) {
            events.add(new LeadTimelineEventDto(
                    message.getDataEnvio(),
                    "Mensagem",
                    "Mensagem WhatsApp",
                    message.getConteudo()
            ));
        }

        for (AgendamentoEntity schedule : schedules) {
            events.add(new LeadTimelineEventDto(
                    schedule.getData_hora(),
                    "Agendamento",
                    "Consulta agendada",
                    schedule.getMedico() != null ? schedule.getMedico().getNome() : "Medico nao informado"
            ));
        }

        events.sort(Comparator.comparing(LeadTimelineEventDto::dataHora, Comparator.nullsLast(Comparator.reverseOrder())));
        return events.stream().limit(15).toList();
    }

    private LeadConversionItemDto buildConversion(String from, String to, long fromCount, long toCount) {
        if (fromCount <= 0) {
            return new LeadConversionItemDto(from, to, BigDecimal.ZERO);
        }

        BigDecimal conversion = BigDecimal.valueOf(toCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(fromCount), 2, RoundingMode.HALF_UP);
        return new LeadConversionItemDto(from, to, conversion);
    }

    private LocalDateTime maxDateTime(LocalDateTime current, LocalDateTime candidate) {
        if (current == null) {
            return candidate;
        }
        if (candidate == null) {
            return current;
        }
        return candidate.isAfter(current) ? candidate : current;
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

    public LeadsAnalyticsResponseDto getLeadsAnalytics(
            LocalDate from,
            LocalDate to,
            String canal,
            String origem,
            String responsavel,
            String etapa,
            String status,
            String cidade,
            String tag,
            Integer scoreMin,
            Integer scoreMax,
            BigDecimal valorMin,
            BigDecimal valorMax
    ) {
        LocalDateTime start = toStartOfDay(from);
        LocalDateTime end = toEndOfDay(to);
        LocalDateTime now = LocalDateTime.now();

        List<PacienteEntity> patients = pacienteRepository.findAll().stream()
                .filter(item -> inRange(item.getCriadoEm(), start, end))
                .toList();

        Map<Long, List<JornadaPacienteEntity>> journeysByPatient = new HashMap<>();
        for (JornadaPacienteEntity journey : jornadaRepository.findAll()) {
            if (journey.getPaciente() == null || journey.getPaciente().getId() == null) {
                continue;
            }

            if (!inRange(journey.getDataInicio(), start, end)) {
                continue;
            }

            Long patientId = journey.getPaciente().getId();
            journeysByPatient.computeIfAbsent(patientId, key -> new ArrayList<>()).add(journey);
        }

        journeysByPatient.values().forEach(list ->
                list.sort(Comparator.comparing(JornadaPacienteEntity::getDataInicio, Comparator.nullsLast(Comparator.naturalOrder()))));

        Map<Long, List<ConversaEntity>> conversationsByPatient = new HashMap<>();
        for (ConversaEntity conversation : conversaRepository.findAll()) {
            if (conversation.getContato() == null || conversation.getContato().getPaciente() == null) {
                continue;
            }

            Long patientId = conversation.getContato().getPaciente().getId();
            if (patientId == null) {
                continue;
            }

            if (!inRange(conversation.getAtualizadoEm(), start, end) && (start != null || end != null)) {
                continue;
            }

            conversationsByPatient.computeIfAbsent(patientId, key -> new ArrayList<>()).add(conversation);
        }

        conversationsByPatient.values().forEach(list ->
                list.sort(Comparator.comparing(ConversaEntity::getAtualizadoEm, Comparator.nullsLast(Comparator.naturalOrder()))));

        Map<Long, ConversaEntity> conversationById = new HashMap<>();
        conversationsByPatient.values().forEach(list -> list.forEach(conversation -> conversationById.put(conversation.getId(), conversation)));

        Map<Long, List<MensagemEntity>> messagesByPatient = new HashMap<>();
        for (MensagemEntity message : mensagemRepository.findAll()) {
            if (message.getConversa() == null || message.getConversa().getId() == null) {
                continue;
            }

            ConversaEntity conversation = conversationById.get(message.getConversa().getId());
            if (conversation == null || conversation.getContato() == null || conversation.getContato().getPaciente() == null) {
                continue;
            }

            if (!inRange(message.getDataEnvio(), start, end) && (start != null || end != null)) {
                continue;
            }

            Long patientId = conversation.getContato().getPaciente().getId();
            messagesByPatient.computeIfAbsent(patientId, key -> new ArrayList<>()).add(message);
        }

        messagesByPatient.values().forEach(list ->
                list.sort(Comparator.comparing(MensagemEntity::getDataEnvio, Comparator.nullsLast(Comparator.naturalOrder()))));

        Map<Long, List<AgendamentoEntity>> schedulesByPatient = new HashMap<>();
        for (AgendamentoEntity scheduling : agendamentoRepository.findAll()) {
            if (scheduling.getPaciente() == null || scheduling.getPaciente().getId() == null) {
                continue;
            }

            if (!inRange(scheduling.getData_hora(), start, end) && (start != null || end != null)) {
                continue;
            }

            schedulesByPatient.computeIfAbsent(scheduling.getPaciente().getId(), key -> new ArrayList<>()).add(scheduling);
        }

        schedulesByPatient.values().forEach(list ->
                list.sort(Comparator.comparing(AgendamentoEntity::getData_hora, Comparator.nullsLast(Comparator.naturalOrder()))));

        List<LeadAnalyticsItemDto> leads = new ArrayList<>();
        for (PacienteEntity patient : patients) {
            long patientId = patient.getId();
            List<JornadaPacienteEntity> patientJourneys = journeysByPatient.getOrDefault(patientId, List.of());
            JornadaPacienteEntity journey = patientJourneys.isEmpty() ? null : patientJourneys.get(patientJourneys.size() - 1);
            List<ConversaEntity> patientConversations = conversationsByPatient.getOrDefault(patientId, List.of());
            List<MensagemEntity> patientMessages = messagesByPatient.getOrDefault(patientId, List.of());
            List<AgendamentoEntity> patientSchedules = schedulesByPatient.getOrDefault(patientId, List.of());

            boolean hasConversation = !patientConversations.isEmpty();
            boolean hasFutureSchedule = patientSchedules.stream().anyMatch(item -> item.getData_hora() != null && item.getData_hora().isAfter(now));
            AgendamentoEntity nextSchedule = patientSchedules.stream()
                    .filter(item -> item.getData_hora() != null && item.getData_hora().isAfter(now))
                    .min(Comparator.comparing(AgendamentoEntity::getData_hora))
                    .orElse(null);

            LeadStage stage = resolveStage(hasConversation, journey != null ? journey.getEtapa() : null);
            BigDecimal potential = stage.valorBase
                    .add(hasConversation ? new BigDecimal("1500") : BigDecimal.ZERO)
                    .add(journey != null ? new BigDecimal("2200") : BigDecimal.ZERO)
                    .add(hasFutureSchedule ? new BigDecimal("2600") : BigDecimal.ZERO);
            BigDecimal weighted = potential.multiply(stage.probabilidade).setScale(0, RoundingMode.HALF_UP);

            Integer score = calculateLeadScore(stage, hasConversation, hasFutureSchedule, patientMessages.size(), patient.getCriadoEm(), now);
            String leadStatus = score >= 80 ? "Quente" : score >= 55 ? "Morno" : "Frio";
            String leadOrigin = resolveLeadOrigin(patient, hasConversation);
            String leadChannel = hasConversation ? "WhatsApp" : "Site/Recepcao";
            String leadOwner = resolveLeadOwner(patientMessages, nextSchedule, patientId);
            NextAction nextAction = resolveNextAction(stage, hasConversation, nextSchedule, now);

            LocalDateTime latestInteraction = resolveLatestInteraction(patient, patientJourneys, patientConversations, patientMessages, patientSchedules);
            long daysWithoutInteraction = latestInteraction == null ? 999 : java.time.Duration.between(latestInteraction, now).toDays();

            List<String> tags = resolveTags(stage, hasConversation, hasFutureSchedule, weighted, leadStatus);
            List<LeadAlertDto> alerts = resolveAlerts(nextAction, hasConversation, score, hasFutureSchedule, daysWithoutInteraction);
            List<LeadTimelineEventDto> timeline = buildTimeline(patient, patientJourneys, patientConversations, patientMessages, patientSchedules);

            String leadCity = patient.getEnderecos() != null && !patient.getEnderecos().isEmpty()
                    ? patient.getEnderecos().stream().map(EnderecoEntity::getCidade).filter(Objects::nonNull).findFirst().orElse("-")
                    : "-";

            LeadAnalyticsItemDto lead = new LeadAnalyticsItemDto(
                    patient.getId(),
                    patient.getNome(),
                    patient.getTelefone(),
                    patient.getEmail(),
                    leadChannel,
                    leadOrigin,
                    leadOwner,
                    stage.label,
                    leadStatus,
                    stage.probabilidade,
                    score,
                    potential,
                    weighted,
                    nextAction.acao(),
                    nextAction.dataHora(),
                    nextAction.dataHora() != null && nextAction.dataHora().isBefore(now),
                    daysWithoutInteraction,
                    patientMessages.size(),
                    leadCity,
                    tags,
                    timeline,
                    alerts
            );

            if (!matchesFilters(lead, canal, origem, responsavel, etapa, status, cidade, tag, scoreMin, scoreMax, valorMin, valorMax)) {
                continue;
            }

            leads.add(lead);
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

        long slaAtrasados = leads.stream().filter(item -> Boolean.TRUE.equals(item.slaAtrasado())).count();
        long semFollowUp = leads.stream()
            .filter(item -> item.alertas() != null && item.alertas().stream().anyMatch(alert -> "SEM_FOLLOW_UP".equals(alert.tipo())))
            .count();

        List<LeadConversionItemDto> conversion = List.of(
            buildConversion("Novo", "Contato", novos, contato),
            buildConversion("Contato", "Triagem", contato, triagem),
            buildConversion("Triagem", "Qualificado", triagem, qualificados)
        );

        Map<String, List<LeadAnalyticsItemDto>> kanban = new LinkedHashMap<>();
        kanban.put("Novo", leads.stream().filter(item -> "Novo".equals(item.etapa())).toList());
        kanban.put("Contato", leads.stream().filter(item -> "Contato".equals(item.etapa())).toList());
        kanban.put("Triagem", leads.stream().filter(item -> "Triagem".equals(item.etapa())).toList());
        kanban.put("Qualificado", leads.stream().filter(item -> "Qualificado".equals(item.etapa())).toList());

        return new LeadsAnalyticsResponseDto(
                (long) leads.size(),
                novos,
                contato,
                triagem,
                qualificados,
            slaAtrasados,
            semFollowUp,
                pipelineBruto,
                pipelinePonderado,
            conversion,
            kanban,
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

    private record NextAction(String acao, LocalDateTime dataHora) {
    }

    private static class FinanceAccumulator {
        long realizadas = 0;
        long agendadas = 0;
        BigDecimal receita = BigDecimal.ZERO;
        BigDecimal custo = BigDecimal.ZERO;
    }
}
