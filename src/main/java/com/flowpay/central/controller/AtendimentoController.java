package com.flowpay.central.controller;

import com.flowpay.central.dto.atendimento.AtendimentoCreateRequest;
import com.flowpay.central.dto.atendimento.AtendimentoResponse;
import com.flowpay.central.service.AtendimentoService;
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
@RequestMapping("/api/atendimentos")
@RequiredArgsConstructor
@Tag(name = "Atendimentos", description = "Distribuicao e gestao de atendimentos")
public class AtendimentoController {

    private final AtendimentoService atendimentoService;

    @PostMapping
    @Operation(summary = "Criar atendimento")
    public ResponseEntity<AtendimentoResponse> criar(@Valid @RequestBody AtendimentoCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(atendimentoService.criar(request));
    }

    @GetMapping
    @Operation(summary = "Listar atendimentos")
    public ResponseEntity<List<AtendimentoResponse>> listar() {
        return ResponseEntity.ok(atendimentoService.listar());
    }

    @PatchMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar atendimento")
    public ResponseEntity<AtendimentoResponse> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(atendimentoService.finalizar(id));
    }
}
