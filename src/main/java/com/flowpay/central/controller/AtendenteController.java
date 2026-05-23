package com.flowpay.central.controller;

import com.flowpay.central.dto.atendente.AtendenteCreateRequest;
import com.flowpay.central.dto.atendente.AtendenteResponse;
import com.flowpay.central.service.AtendenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/atendentes")
@RequiredArgsConstructor
@Tag(name = "Atendentes", description = "Gestao de atendentes da central")
public class AtendenteController {

    private final AtendenteService atendenteService;

    @PostMapping
    @Operation(summary = "Criar atendente")
    public ResponseEntity<AtendenteResponse> criar(@Valid @RequestBody AtendenteCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(atendenteService.criar(request));
    }

    @GetMapping
    @Operation(summary = "Listar atendentes")
    public ResponseEntity<List<AtendenteResponse>> listar() {
        return ResponseEntity.ok(atendenteService.listar());
    }

    @PatchMapping("/{id}/ativar")
    @Operation(summary = "Ativar atendente")
    public ResponseEntity<AtendenteResponse> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(atendenteService.ativar(id));
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar atendente")
    public ResponseEntity<AtendenteResponse> desativar(@PathVariable Long id) {
        return ResponseEntity.ok(atendenteService.desativar(id));
    }
}
