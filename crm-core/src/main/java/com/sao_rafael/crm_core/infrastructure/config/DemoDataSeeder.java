package com.sao_rafael.crm_core.infrastructure.config;

import com.sao_rafael.crm_core.infrastructure.persistence.entity.*;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DemoDataSeeder implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoDataSeeder.class);

    private final PacienteJpaRepository pacienteRepository;
    private final CargoJpaRepository cargoRepository;
    private final FuncionarioJpaRepository funcionarioRepository;
    private final MedicoJpaRepository medicoRepository;
    private final ProcedimentoJpaRepository procedimentoRepository;
    private final StatusJpaRepository statusRepository;
    private final EtapaJpaRepository etapaRepository;
    private final CirurgiaJpaRepository cirurgiaRepository;
    private final JornadaPacienteJpaRepository jornadaRepository;
    private final ContatoWhatsappJpaRepository contatoRepository;
    private final ConversaJpaRepository conversaRepository;
    private final AgendamentoJpaRepository agendamentoRepository;
    private final AtendimentoJpaRepository atendimentoRepository;
    private final GravidadeJpaRepository gravidadeRepository;
    private final ComplicacaoCirurgiaJpaRepository complicacaoRepository;
    private final ConsentimentoComunicacaoJpaRepository consentimentoRepository;
    private final MensagemJpaRepository mensagemRepository;
    private final UsuarioJpaRepository usuarioRepository;

    public DemoDataSeeder(
            PacienteJpaRepository pacienteRepository,
            CargoJpaRepository cargoRepository,
            FuncionarioJpaRepository funcionarioRepository,
            MedicoJpaRepository medicoRepository,
            ProcedimentoJpaRepository procedimentoRepository,
            StatusJpaRepository statusRepository,
            EtapaJpaRepository etapaRepository,
            CirurgiaJpaRepository cirurgiaRepository,
            JornadaPacienteJpaRepository jornadaRepository,
            ContatoWhatsappJpaRepository contatoRepository,
                ConversaJpaRepository conversaRepository,
                AgendamentoJpaRepository agendamentoRepository,
                AtendimentoJpaRepository atendimentoRepository,
                GravidadeJpaRepository gravidadeRepository,
                ComplicacaoCirurgiaJpaRepository complicacaoRepository,
                ConsentimentoComunicacaoJpaRepository consentimentoRepository,
                MensagemJpaRepository mensagemRepository,
                UsuarioJpaRepository usuarioRepository
    ) {
        this.pacienteRepository = pacienteRepository;
        this.cargoRepository = cargoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.medicoRepository = medicoRepository;
        this.procedimentoRepository = procedimentoRepository;
        this.statusRepository = statusRepository;
        this.etapaRepository = etapaRepository;
        this.cirurgiaRepository = cirurgiaRepository;
        this.jornadaRepository = jornadaRepository;
        this.contatoRepository = contatoRepository;
        this.conversaRepository = conversaRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.atendimentoRepository = atendimentoRepository;
        this.gravidadeRepository = gravidadeRepository;
        this.complicacaoRepository = complicacaoRepository;
        this.consentimentoRepository = consentimentoRepository;
        this.mensagemRepository = mensagemRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        LOGGER.info("Seeder de demo: preparando registros para analytics...");

        StatusEntity statusOperacional = ensureStatus("EM_ANDAMENTO", "Status de operacao", StatusEntity.CategoriaStatus.CONSULTA);
        List<EtapaEntity> etapas = ensureEtapas(List.of("Novo", "Contato", "Triagem", "Qualificado"));
        List<ProcedimentoEntity> procedimentos = ensureProcedimentos(List.of(
                "Artroplastia de Joelho",
                "Colecistectomia",
                "Herniorrafia",
                "Cirurgia Bariatrica",
                "Cirurgia Cardiaca"
        ));

        CargoEntity cargoMedico = ensureCargo("Medico Cirurgiao", "Assistencial", "Senior");
        CargoEntity cargoEnfermeiro = ensureCargo("Enfermeiro", "Assistencial", "Pleno");
        CargoEntity cargoMarketing = ensureCargo("Analista Comercial", "Comercial", "Pleno");

        ensureFuncionarios(cargoEnfermeiro, cargoMarketing);
        List<MedicoEntity> medicos = ensureMedicos(cargoMedico);
        List<PacienteEntity> pacientes = ensurePacientes();
        List<AgendamentoEntity> agendamentos = ensureAgendamentos(pacientes, medicos, etapas, statusOperacional);

        ensureCirurgias(pacientes, medicos, procedimentos, statusOperacional);
        List<GravidadeEntity> gravidades = ensureGravidades();
        ensureComplicacoes(gravidades);
        ensureAtendimentos(agendamentos, etapas);
        ensureJornadas(pacientes, etapas);
        ensureConversas(pacientes, statusOperacional);

        UsuarioEntity senderUser = ensureChatSenderUser();
        ensureConsentimentos(pacientes, senderUser);
        ensureMensagensEmConversas(senderUser);

        LOGGER.info("Seeder de demo finalizado com sucesso.");
    }

    private StatusEntity ensureStatus(String nome, String descricao, StatusEntity.CategoriaStatus categoria) {
        return statusRepository.findAll().stream()
                .filter(item -> nome.equalsIgnoreCase(item.getNome()))
                .findFirst()
                .orElseGet(() -> {
                    StatusEntity status = new StatusEntity();
                    status.setNome(nome);
                    status.setDescricao(descricao);
                    status.setCategoria(categoria);
                    return statusRepository.save(status);
                });
    }

    private List<EtapaEntity> ensureEtapas(List<String> nomes) {
        List<EtapaEntity> existing = etapaRepository.findAll();
        List<EtapaEntity> result = new ArrayList<>();

        for (String nome : nomes) {
            EtapaEntity etapa = existing.stream()
                    .filter(item -> nome.equalsIgnoreCase(item.getNome()))
                    .findFirst()
                    .orElseGet(() -> {
                        EtapaEntity novo = new EtapaEntity();
                        novo.setNome(nome);
                        return etapaRepository.save(novo);
                    });
            result.add(etapa);
        }

        return result;
    }

    private List<ProcedimentoEntity> ensureProcedimentos(List<String> nomes) {
        List<ProcedimentoEntity> existing = procedimentoRepository.findAll();
        List<ProcedimentoEntity> result = new ArrayList<>();

        for (String nome : nomes) {
            ProcedimentoEntity procedimento = existing.stream()
                    .filter(item -> nome.equalsIgnoreCase(item.getNomeProcedimento()))
                    .findFirst()
                    .orElseGet(() -> {
                        ProcedimentoEntity novo = new ProcedimentoEntity();
                        novo.setNomeProcedimento(nome);
                        novo.setStatus(ProcedimentoEntity.StatusProcedimento.ATIVO);
                        return procedimentoRepository.save(novo);
                    });
            result.add(procedimento);
        }

        return result;
    }

    private CargoEntity ensureCargo(String cargoNome, String departamento, String nivel) {
        return cargoRepository.findAll().stream()
                .filter(item -> cargoNome.equalsIgnoreCase(item.getCargo()) && departamento.equalsIgnoreCase(item.getDepartamento()))
                .findFirst()
                .orElseGet(() -> {
                    CargoEntity cargo = new CargoEntity();
                    cargo.setCargo(cargoNome);
                    cargo.setDepartamento(departamento);
                    cargo.setNivelHierarquico(nivel);
                    return cargoRepository.save(cargo);
                });
    }

    private void ensureFuncionarios(CargoEntity cargoEnfermeiro, CargoEntity cargoMarketing) {
        if (funcionarioRepository.count() >= 12) {
            return;
        }

        int target = 12;
        int index = 1;
        while (funcionarioRepository.count() < target) {
            String cpf = String.format("900000%05d", index);
            index += 1;

            if (funcionarioRepository.existsByCpf(cpf)) {
                continue;
            }

            FuncionarioEntity funcionario = new FuncionarioEntity();
            funcionario.setNome("Funcionario Demo " + index);
            funcionario.setCpf(cpf);
            funcionario.setCargo(index % 2 == 0 ? cargoEnfermeiro : cargoMarketing);
            funcionario.setAtivo(Boolean.TRUE);
            funcionarioRepository.save(funcionario);
        }
    }

    private List<MedicoEntity> ensureMedicos(CargoEntity cargoMedico) {
        if (medicoRepository.count() >= 5) {
            return medicoRepository.findAll();
        }

        int index = 1;
        while (medicoRepository.count() < 5) {
            String cpf = String.format("910000%05d", index);
            index += 1;

            if (funcionarioRepository.existsByCpf(cpf)) {
                continue;
            }

            MedicoEntity medico = new MedicoEntity();
            medico.setNome("Dr Demo " + index);
            medico.setCpf(cpf);
            medico.setCargo(cargoMedico);
            medico.setAtivo(Boolean.TRUE);
            medicoRepository.save(medico);
        }

        return medicoRepository.findAll();
    }

    private List<PacienteEntity> ensurePacientes() {
        if (pacienteRepository.count() >= 24) {
            return pacienteRepository.findAll();
        }

        int index = 1;
        while (pacienteRepository.count() < 24) {
            String cpf = String.format("700000%05d", index);
            String email = "paciente.demo" + index + "@hsr.local";
            index += 1;

            boolean cpfTaken = pacienteRepository.findAll().stream().anyMatch(item -> cpf.equals(item.getCpf()));
            boolean emailTaken = pacienteRepository.findAll().stream().anyMatch(item -> email.equalsIgnoreCase(item.getEmail()));
            if (cpfTaken || emailTaken) {
                continue;
            }

            PacienteEntity paciente = new PacienteEntity();
            paciente.setNome("Paciente Demo " + index);
            paciente.setCpf(cpf);
            paciente.setEmail(email);
            paciente.setSexo(index % 2 == 0 ? PacienteEntity.Sexo.FEMININO : PacienteEntity.Sexo.MASCULINO);
            paciente.setDataNascimento(LocalDate.now().minusYears(20 + (index % 40)));
            paciente.setTelefone("+55119990" + String.format("%04d", index));
            paciente.setAlturaCm(160 + (index % 15));
            paciente.setPesoKg(new BigDecimal(55 + (index % 35)));
            pacienteRepository.save(paciente);
        }

        return pacienteRepository.findAll();
    }

    private void ensureCirurgias(
            List<PacienteEntity> pacientes,
            List<MedicoEntity> medicos,
            List<ProcedimentoEntity> procedimentos,
            StatusEntity status
    ) {
        if (pacientes.isEmpty() || medicos.isEmpty() || procedimentos.isEmpty()) {
            return;
        }

        int target = 30;
        while (cirurgiaRepository.count() < target) {
            int currentSize = (int) cirurgiaRepository.count();
            PacienteEntity paciente = pacientes.get(currentSize % pacientes.size());
            MedicoEntity medico = medicos.get(currentSize % medicos.size());
            ProcedimentoEntity procedimento = procedimentos.get(currentSize % procedimentos.size());

            LocalDateTime scheduledDate = LocalDateTime.now().minusDays(ThreadLocalRandom.current().nextInt(10, 120));
            boolean alreadyDone = ThreadLocalRandom.current().nextBoolean();

            CirurgiaEntity cirurgia = new CirurgiaEntity();
            cirurgia.setPaciente(paciente);
            cirurgia.setMedico(medico);
            cirurgia.setProcedimento(procedimento);
            cirurgia.setStatus(status);
            cirurgia.setRisco(CirurgiaEntity.RiscoCirurgia.values()[currentSize % CirurgiaEntity.RiscoCirurgia.values().length]);
            cirurgia.setSala("Sala " + ((currentSize % 5) + 1));
            cirurgia.setDataAgendada(scheduledDate);
            cirurgia.setDataRealizada(alreadyDone ? scheduledDate.plusHours(4) : null);

            cirurgiaRepository.save(cirurgia);
        }
    }

    private List<AgendamentoEntity> ensureAgendamentos(
            List<PacienteEntity> pacientes,
            List<MedicoEntity> medicos,
            List<EtapaEntity> etapas,
            StatusEntity status
    ) {
        if (pacientes.isEmpty() || medicos.isEmpty() || etapas.isEmpty() || status == null) {
            return agendamentoRepository.findAll();
        }

        LocalDate now = LocalDate.now();
        int targetCurrentMonth = 28;
        int index = 0;

        while (countAgendamentosNoMesAtual(now) < targetCurrentMonth) {
            PacienteEntity paciente = pacientes.get(index % pacientes.size());
            MedicoEntity medico = medicos.get(index % medicos.size());
            EtapaEntity etapa = etapas.get(index % etapas.size());

            LocalDateTime baseDate = LocalDateTime.now()
                    .withDayOfMonth(1)
                    .plusDays(index % 42)
                    .withHour(8 + (index % 10))
                    .withMinute(index % 2 == 0 ? 0 : 30)
                    .withSecond(0)
                    .withNano(0);

            AgendamentoEntity agendamento = new AgendamentoEntity();
            agendamento.setPaciente(paciente);
            agendamento.setMedico(medico);
            agendamento.setEtapa(etapa);
            agendamento.setStatus(status);
            agendamento.setData_hora(baseDate);
            agendamentoRepository.save(agendamento);
            index += 1;
        }

        return agendamentoRepository.findAll();
    }

    private long countAgendamentosNoMesAtual(LocalDate now) {
        return agendamentoRepository.findAll().stream()
                .filter(item -> item.getData_hora() != null)
                .filter(item -> item.getData_hora().getYear() == now.getYear() && item.getData_hora().getMonthValue() == now.getMonthValue())
                .count();
    }

    private List<GravidadeEntity> ensureGravidades() {
        List<GravidadeEntity> existing = gravidadeRepository.findAll();
        if (existing.size() >= 4) {
            return existing;
        }

        List<String[]> defaults = List.of(
                new String[]{"Leve", "Risco clinico baixo"},
                new String[]{"Moderada", "Risco clinico moderado"},
                new String[]{"Alta", "Risco clinico alto"},
                new String[]{"Critica", "Risco clinico critico"}
        );

        for (String[] item : defaults) {
            boolean exists = gravidadeRepository.findAll().stream().anyMatch(current -> item[0].equalsIgnoreCase(current.getNome()));
            if (exists) {
                continue;
            }

            GravidadeEntity entity = new GravidadeEntity();
            entity.setNome(item[0]);
            entity.setDescricao(item[1]);
            gravidadeRepository.save(entity);
        }

        return gravidadeRepository.findAll();
    }

    private void ensureComplicacoes(List<GravidadeEntity> gravidades) {
        if (cirurgiaRepository.count() == 0 || gravidades.isEmpty()) {
            return;
        }

        int target = 14;
        int index = 0;
        List<CirurgiaEntity> cirurgias = cirurgiaRepository.findAll();

        while (complicacaoRepository.count() < target) {
            CirurgiaEntity cirurgia = cirurgias.get(index % cirurgias.size());

            ComplicacaoCirurgiaEntity complicacao = new ComplicacaoCirurgiaEntity();
            complicacao.setCirurgia(cirurgia);
            complicacao.setDescricao(index % 2 == 0
                    ? "Sangramento monitorado no pos-operatorio"
                    : "Dor intensa controlada com ajuste de analgesia");
            complicacaoRepository.save(complicacao);
            index += 1;
        }
    }

    private void ensureAtendimentos(List<AgendamentoEntity> agendamentos, List<EtapaEntity> etapas) {
        if (agendamentos.isEmpty() || etapas.isEmpty()) {
            return;
        }

        int target = 24;
        List<Long> agendamentoIdsEmUso = new ArrayList<>(atendimentoRepository.findAll().stream()
                .map(item -> item.getAgendamento() != null ? item.getAgendamento().getId() : null)
                .filter(id -> id != null)
            .toList());

        int index = 0;
        while (atendimentoRepository.count() < target) {
            AgendamentoEntity agendamento = agendamentos.get(index % agendamentos.size());
            if (agendamentoIdsEmUso.contains(agendamento.getId())) {
                index += 1;
                if (index > agendamentos.size() * 2) {
                    break;
                }
                continue;
            }

            AtendimentoEntity atendimento = new AtendimentoEntity();
            atendimento.setAgendamento(agendamento);
            atendimento.setPaciente(agendamento.getPaciente());
            atendimento.setMedico(agendamento.getMedico());
            atendimento.setEtapa(etapas.get(index % etapas.size()));

            LocalDateTime inicio = agendamento.getData_hora() != null ? agendamento.getData_hora() : LocalDateTime.now().minusDays(index + 1);
            atendimento.setDataInicio(inicio);
            atendimento.setDataFim(index % 3 == 0 ? inicio.plusHours(1) : null);
            atendimento.setObservacoes("Atendimento de acompanhamento gerado para ambiente de testes.");
            atendimentoRepository.save(atendimento);

            agendamentoIdsEmUso.add(agendamento.getId());
            index += 1;
        }
    }

    private void ensureConsentimentos(List<PacienteEntity> pacientes, UsuarioEntity senderUser) {
        if (pacientes.isEmpty() || senderUser == null) {
            return;
        }

        int target = 20;
        int index = 0;
        while (consentimentoRepository.count() < target) {
            PacienteEntity paciente = pacientes.get(index % pacientes.size());

            ConsentimentoComunicacaoEntity consentimento = new ConsentimentoComunicacaoEntity();
            consentimento.setPaciente_id(paciente.getId());
            consentimento.setConcedido(index % 4 != 0);
            consentimento.setCanal(ConsentimentoComunicacaoEntity.CanalComunicacao.values()[index % ConsentimentoComunicacaoEntity.CanalComunicacao.values().length]);
            consentimento.setFinalidade(ConsentimentoComunicacaoEntity.FinalidadeComunicacao.values()[index % ConsentimentoComunicacaoEntity.FinalidadeComunicacao.values().length]);
            consentimento.setDataConsentimento(LocalDateTime.now().minusDays(index + 1));
            consentimento.setAtualizadoEm(LocalDateTime.now().minusDays(index / 2 + 1));
            consentimento.setOrigemAtendimento("Portal do Paciente");
            consentimento.setRegistradoPor(senderUser);
            consentimentoRepository.save(consentimento);
            index += 1;
        }
    }

    private void ensureJornadas(List<PacienteEntity> pacientes, List<EtapaEntity> etapas) {
        if (pacientes.isEmpty() || etapas.isEmpty()) {
            return;
        }

        if (jornadaRepository.count() >= 40) {
            return;
        }

        int index = 0;
        while (jornadaRepository.count() < 40) {
            PacienteEntity paciente = pacientes.get(index % pacientes.size());
            EtapaEntity etapa = etapas.get(index % etapas.size());

            JornadaPacienteEntity jornada = new JornadaPacienteEntity();
            jornada.setPaciente(paciente);
            jornada.setEtapa(etapa);
            jornada.setDataInicio(LocalDateTime.now().minusDays(2L * (index + 1)));
            jornada.setDataFim(index % 3 == 0 ? LocalDateTime.now().minusDays(index) : null);
            jornadaRepository.save(jornada);
            index += 1;
        }
    }

    private void ensureConversas(List<PacienteEntity> pacientes, StatusEntity status) {
        if (pacientes.isEmpty()) {
            return;
        }

        if (conversaRepository.count() >= 14) {
            return;
        }

        int index = 0;
        while (conversaRepository.count() < 14) {
            PacienteEntity paciente = pacientes.get(index % pacientes.size());
            String numero = "+55119888" + String.format("%04d", index + 10);

            ContatoWhatsappEntity contato = contatoRepository.findFirstByNumero(numero)
                    .orElseGet(() -> {
                        ContatoWhatsappEntity novo = new ContatoWhatsappEntity();
                        novo.setPaciente(paciente);
                        novo.setNumero(numero);
                        return contatoRepository.save(novo);
                    });

            ConversaEntity conversa = new ConversaEntity();
            conversa.setContato(contato);
            conversa.setStatus(status);
            conversa.setIniciadoEm(LocalDateTime.now().minusDays(index + 1));
            conversa.setAtualizadoEm(LocalDateTime.now().minusHours(index + 1));
            conversaRepository.save(conversa);
            index += 1;
        }
    }

    private UsuarioEntity ensureChatSenderUser() {
        Optional<UsuarioEntity> active = usuarioRepository.findFirstByAtivoTrueOrderByIdAsc();
        if (active.isPresent()) {
            return active.get();
        }

        Optional<UsuarioEntity> anyUser = usuarioRepository.findAll().stream().findFirst();
        if (anyUser.isPresent()) {
            return anyUser.get();
        }

        FuncionarioEntity funcionarioBase = funcionarioRepository.findAll().stream()
                .filter(item -> item.getUsuario() == null)
                .findFirst()
                .orElseGet(() -> funcionarioRepository.findAll().stream().findFirst().orElse(null));

        if (funcionarioBase == null) {
            LOGGER.warn("Seeder de mensagens: nenhum funcionario disponivel para criar usuario remetente.");
            return null;
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setFuncionario(funcionarioBase);
        usuario.setEmail("sistema.demo@hsr.local");
        usuario.setSenhaHash("seeded-demo-hash");
        usuario.setAtivo(Boolean.TRUE);
        return usuarioRepository.save(usuario);
    }

    private void ensureMensagensEmConversas(UsuarioEntity senderUser) {
        if (senderUser == null) {
            LOGGER.warn("Seeder de mensagens: usuario remetente indisponivel, mensagens nao serao criadas.");
            return;
        }

        List<ConversaEntity> conversas = conversaRepository.findAllByOrderByAtualizadoEmDesc();
        if (conversas.isEmpty()) {
            return;
        }

        for (int index = 0; index < conversas.size(); index += 1) {
            ConversaEntity conversa = conversas.get(index);
            if (mensagemRepository.findFirstByConversa_IdOrderByDataEnvioDesc(conversa.getId()).isPresent()) {
                continue;
            }

            LocalDateTime baseTime = Optional.ofNullable(conversa.getIniciadoEm()).orElse(LocalDateTime.now().minusDays(2));

            MensagemEntity inbound = new MensagemEntity();
            inbound.setConversa(conversa);
            inbound.setEnviadoPor(senderUser);
            inbound.setOrigem("INBOUND");
            inbound.setConteudo("Olá, gostaria de entender melhor sobre o procedimento.");
            inbound.setDataEnvio(baseTime.plusMinutes(3));
            mensagemRepository.save(inbound);

            MensagemEntity outbound = new MensagemEntity();
            outbound.setConversa(conversa);
            outbound.setEnviadoPor(senderUser);
            outbound.setOrigem("OUTBOUND");
            outbound.setConteudo("Perfeito! Posso ajudar com valores, preparo e disponibilidade de agenda.");
            outbound.setDataEnvio(baseTime.plusMinutes(12));
            mensagemRepository.save(outbound);

            MensagemEntity followUp = new MensagemEntity();
            followUp.setConversa(conversa);
            followUp.setEnviadoPor(senderUser);
            followUp.setOrigem(index % 2 == 0 ? "INBOUND" : "OUTBOUND");
            followUp.setConteudo(index % 2 == 0
                    ? "Tenho interesse em marcar a consulta inicial ainda esta semana."
                    : "Tenho vaga na quinta-feira as 14h e sexta-feira as 10h.");
            followUp.setDataEnvio(baseTime.plusMinutes(20));
            mensagemRepository.save(followUp);

            conversa.setAtualizadoEm(baseTime.plusMinutes(20));
            conversaRepository.save(conversa);
        }

        long totalMensagens = mensagemRepository.count();
        LOGGER.info("Seeder de mensagens finalizado. Total atual de mensagens: {}", totalMensagens);
    }
}
