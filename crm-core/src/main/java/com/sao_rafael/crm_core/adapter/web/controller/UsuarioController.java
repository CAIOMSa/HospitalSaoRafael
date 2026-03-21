package com.sao_rafael.crm_core.adapter.web.controller;

import com.sao_rafael.crm_core.application.service.UsuarioService;
import com.sao_rafael.crm_core.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController extends BaseCrudController<UsuarioEntity, Long, UsuarioService> {

    public UsuarioController(UsuarioService service) {
        super(service);
    }

    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> loginUrl(
            @RequestParam(defaultValue = "http://localhost:3000/silent-check-sso.html") String redirectUri) {
        Map<String, String> payload = new HashMap<>();
        payload.put("loginUrl", service.buildLoginUrl(redirectUri));
        payload.put("redirectUri", redirectUri);
        return ResponseEntity.ok(payload);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody(required = false) Map<String, String> body) {
        String redirectUri = "http://localhost:3000/silent-check-sso.html";
        if (body != null && body.get("redirectUri") != null && !body.get("redirectUri").isBlank()) {
            redirectUri = body.get("redirectUri");
        }
        Map<String, String> payload = new HashMap<>();
        payload.put("loginUrl", service.buildLoginUrl(redirectUri));
        payload.put("redirectUri", redirectUri);
        return ResponseEntity.ok(payload);
    }
}
