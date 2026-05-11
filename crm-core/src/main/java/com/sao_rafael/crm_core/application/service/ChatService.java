package com.sao_rafael.crm_core.application.service;

import com.sao_rafael.crm_core.adapter.web.dto.*;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.*;
import com.sao_rafael.crm_core.infrastructure.persistence.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ChatService {

    private final ConversaJpaRepository conversaRepository;
    private final MensagemJpaRepository mensagemRepository;
    private final ContatoWhatsappJpaRepository contatoRepository;
    private final PacienteJpaRepository pacienteRepository;
    private final StatusJpaRepository statusRepository;
    private final UsuarioJpaRepository usuarioRepository;

    @Value("${whatsapp.verify-token:crm_sao_rafael}")
    private String webhookVerifyToken;

    public ChatService(
            ConversaJpaRepository conversaRepository,
            MensagemJpaRepository mensagemRepository,
            ContatoWhatsappJpaRepository contatoRepository,
            PacienteJpaRepository pacienteRepository,
            StatusJpaRepository statusRepository,
            UsuarioJpaRepository usuarioRepository
    ) {
        this.conversaRepository = conversaRepository;
        this.mensagemRepository = mensagemRepository;
        this.contatoRepository = contatoRepository;
        this.pacienteRepository = pacienteRepository;
        this.statusRepository = statusRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<ChatConversationDto> listConversations() {
        return conversaRepository.findAllByOrderByAtualizadoEmDesc().stream().map(conversa -> {
            MensagemEntity lastMessage = mensagemRepository
                    .findFirstByConversa_IdOrderByDataEnvioDesc(conversa.getId())
                    .orElse(null);

            return new ChatConversationDto(
                    conversa.getId(),
                    conversa.getContato().getNumero(),
                    conversa.getContato().getPaciente().getNome(),
                    lastMessage != null ? lastMessage.getConteudo() : "Sem mensagens",
                    lastMessage != null ? lastMessage.getDataEnvio() : conversa.getAtualizadoEm()
            );
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> listMessages(Long conversaId) {
        return mensagemRepository.findByConversa_IdOrderByDataEnvioAsc(conversaId)
                .stream()
                .map(this::mapMessage)
                .toList();
    }

    public ChatConversationDto createConversation(CreateConversationRequest request) {
        if (request.numeroContato() == null || request.numeroContato().isBlank()) {
            throw new IllegalArgumentException("numeroContato é obrigatório");
        }

        if (request.pacienteId() == null) {
            throw new IllegalArgumentException("pacienteId é obrigatório");
        }

        ContatoWhatsappEntity contato = contatoRepository.findFirstByNumero(request.numeroContato())
                .orElseGet(() -> {
                    ContatoWhatsappEntity newContato = new ContatoWhatsappEntity();
                    PacienteEntity paciente = pacienteRepository.findById(request.pacienteId())
                            .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado: " + request.pacienteId()));
                    newContato.setPaciente(paciente);
                    newContato.setNumero(request.numeroContato());
                    return contatoRepository.save(newContato);
                });

        ConversaEntity conversa = new ConversaEntity();
        conversa.setContato(contato);
        conversa.setStatus(resolveStatus(request.statusId()));
        conversa.setIniciadoEm(LocalDateTime.now());
        conversa.setAtualizadoEm(LocalDateTime.now());
        ConversaEntity saved = conversaRepository.save(conversa);

        return new ChatConversationDto(
                saved.getId(),
                saved.getContato().getNumero(),
                saved.getContato().getPaciente().getNome(),
                "Sem mensagens",
                saved.getAtualizadoEm()
        );
    }

    public ChatMessageDto sendMessage(SendChatMessageRequest request) {
        if (request.conversaId() == null) {
            throw new IllegalArgumentException("conversaId é obrigatório");
        }

        if (request.conteudo() == null || request.conteudo().isBlank()) {
            throw new IllegalArgumentException("conteúdo é obrigatório");
        }

        ConversaEntity conversa = conversaRepository.findById(request.conversaId())
                .orElseThrow(() -> new IllegalArgumentException("Conversa não encontrada: " + request.conversaId()));

        UsuarioEntity usuario = resolveUsuario(request.usuarioId(), request.usuarioEmail());

        MensagemEntity mensagem = new MensagemEntity();
        mensagem.setConversa(conversa);
        mensagem.setEnviadoPor(usuario);
        mensagem.setConteudo(request.conteudo());
        mensagem.setOrigem("OUTBOUND");
        mensagem.setDataEnvio(LocalDateTime.now());

        MensagemEntity saved = mensagemRepository.save(mensagem);
        conversa.setAtualizadoEm(saved.getDataEnvio());
        conversaRepository.save(conversa);

        return mapMessage(saved);
    }

    public WhatsappWebhookResponse receiveWhatsappWebhook(WhatsappWebhookRequest request) {
        if (request.numeroContato() == null || request.numeroContato().isBlank()) {
            throw new IllegalArgumentException("numeroContato é obrigatório");
        }

        if (request.conteudo() == null || request.conteudo().isBlank()) {
            throw new IllegalArgumentException("conteudo é obrigatório");
        }

        ContatoWhatsappEntity contato = contatoRepository.findFirstByNumero(request.numeroContato())
                .orElseGet(() -> {
                    if (request.pacienteId() == null) {
                        throw new IllegalArgumentException("pacienteId é obrigatório para novo contato");
                    }

                    PacienteEntity paciente = pacienteRepository.findById(request.pacienteId())
                            .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado: " + request.pacienteId()));

                    ContatoWhatsappEntity entity = new ContatoWhatsappEntity();
                    entity.setNumero(request.numeroContato());
                    entity.setPaciente(paciente);
                    return contatoRepository.save(entity);
                });

        ConversaEntity conversa = conversaRepository.findFirstByContato_IdOrderByAtualizadoEmDesc(contato.getId())
                .orElseGet(() -> {
                    ConversaEntity nova = new ConversaEntity();
                    nova.setContato(contato);
                    nova.setStatus(resolveStatus(request.statusId()));
                    nova.setIniciadoEm(LocalDateTime.now());
                    nova.setAtualizadoEm(LocalDateTime.now());
                    return conversaRepository.save(nova);
                });

        UsuarioEntity sistema = resolveUsuario(null, null);

        MensagemEntity inbound = new MensagemEntity();
        inbound.setConversa(conversa);
        inbound.setEnviadoPor(sistema);
        inbound.setConteudo(request.conteudo());
        inbound.setOrigem("INBOUND");
        inbound.setDataEnvio(LocalDateTime.now());

        MensagemEntity saved = mensagemRepository.save(inbound);
        conversa.setAtualizadoEm(saved.getDataEnvio());
        conversaRepository.save(conversa);

        return new WhatsappWebhookResponse(
                "received",
                conversa.getId(),
                saved.getId(),
                "Mensagem recebida e persistida"
        );
    }

    public boolean isVerifyTokenValid(String token) {
        return webhookVerifyToken != null && webhookVerifyToken.equals(token);
    }

    private StatusEntity resolveStatus(Long statusId) {
        if (statusId != null) {
            return statusRepository.findById(statusId)
                    .orElseThrow(() -> new IllegalArgumentException("Status não encontrado: " + statusId));
        }

        return statusRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nenhum status cadastrado para criar conversa"));
    }

    private UsuarioEntity resolveUsuario(Long usuarioId, String usuarioEmail) {
        if (usuarioEmail != null && !usuarioEmail.isBlank()) {
            return usuarioRepository.findFirstByEmailIgnoreCase(usuarioEmail)
                    .orElseGet(() -> resolveUsuarioByIdOrFallback(usuarioId));
        }

        return resolveUsuarioByIdOrFallback(usuarioId);
    }

    private UsuarioEntity resolveUsuarioByIdOrFallback(Long usuarioId) {
        if (usuarioId != null) {
            return usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));
        }

        return usuarioRepository.findFirstByAtivoTrueOrderByIdAsc()
                .orElseGet(() -> usuarioRepository.findAll().stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Nenhum usuário encontrado para enviar mensagem")));
    }

    private ChatMessageDto mapMessage(MensagemEntity message) {
        String autor = message.getEnviadoPor() != null ? message.getEnviadoPor().getEmail() : "sistema";

        return new ChatMessageDto(
                message.getId(),
                message.getConversa().getId(),
                message.getConteudo(),
                message.getDataEnvio(),
                message.getOrigem() != null ? message.getOrigem() : "OUTBOUND",
                autor
        );
    }
}
