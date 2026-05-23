package com.flowpay.central.controller;

import com.flowpay.central.dto.dashboard.DashboardResponse;
import com.flowpay.central.service.DashboardService;
import com.flowpay.central.service.DashboardStreamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Indicadores da operacao em tempo real")
public class DashboardController {

    private final DashboardService dashboardService;
    private final DashboardStreamService dashboardStreamService;

    @GetMapping
    @Operation(summary = "Consultar dashboard consolidado")
    public ResponseEntity<DashboardResponse> buscarDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Stream SSE do dashboard")
    public SseEmitter stream() {
        return dashboardStreamService.subscribe();
    }
}
