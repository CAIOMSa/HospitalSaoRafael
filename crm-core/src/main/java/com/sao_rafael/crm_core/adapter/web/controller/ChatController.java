package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.adapter.web.dto.*;
import com.sao_rafael.crm_core.application.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/conversas")
    public ResponseEntity<List<ChatConversationDto>> listConversations() {
        return ResponseEntity.ok(chatService.listConversations());
    }

    @PostMapping("/conversas")
    public ResponseEntity<ChatConversationDto> createConversation(@RequestBody CreateConversationRequest request) {
        return ResponseEntity.ok(chatService.createConversation(request));
    }

    @GetMapping("/conversas/{conversaId}/mensagens")
    public ResponseEntity<List<ChatMessageDto>> listMessages(@PathVariable Long conversaId) {
        return ResponseEntity.ok(chatService.listMessages(conversaId));
    }

    @PostMapping("/mensagens")
    public ResponseEntity<ChatMessageDto> sendMessage(@RequestBody SendChatMessageRequest request) {
        return ResponseEntity.ok(chatService.sendMessage(request));
    }

    @PostMapping("/webhook/whatsapp")
    public ResponseEntity<WhatsappWebhookResponse> receiveWhatsappWebhook(@RequestBody WhatsappWebhookRequest request) {
        return ResponseEntity.ok(chatService.receiveWhatsappWebhook(request));
    }

    @GetMapping("/webhook/whatsapp")
    public ResponseEntity<String> verifyWhatsappWebhook(
            @RequestParam(name = "hub.mode", required = false) String mode,
            @RequestParam(name = "hub.verify_token", required = false) String verifyToken,
            @RequestParam(name = "hub.challenge", required = false) String challenge
    ) {
        if ("subscribe".equals(mode) && verifyToken != null && chatService.isVerifyTokenValid(verifyToken)) {
            return ResponseEntity.ok(challenge != null ? challenge : "ok");
        }

        return ResponseEntity.status(403).body("forbidden");
    }
}
