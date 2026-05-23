package com.flowpay.central.dto.dashboard;

import java.util.List;
import lombok.Builder;

@Builder
public record DashboardResponse(
        long totalAguardando,
        long totalEmAtendimento,
        long totalFinalizados,
        List<DashboardTimeResponse> porTime
) {
}
