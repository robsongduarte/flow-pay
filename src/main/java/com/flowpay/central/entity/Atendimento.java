package com.flowpay.central.entity;

import com.flowpay.central.enums.AssuntoAtendimento;
import com.flowpay.central.enums.StatusAtendimento;
import com.flowpay.central.enums.TimeAtendimento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "atendimentos",
        indexes = {
            @Index(name = "idx_atendimento_status_time", columnList = "status,time_atendimento"),
            @Index(name = "idx_atendimento_time_status_criado", columnList = "time_atendimento,status,criado_em")
        }
)
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nomeCliente;

    @Column(nullable = false, length = 30)
    private String documentoCliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private AssuntoAtendimento assunto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TimeAtendimento timeAtendimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusAtendimento status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atendente_id")
    private Atendente atendente;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    private LocalDateTime iniciadoEm;

    private LocalDateTime finalizadoEm;

    @Version
    private Long version;

    @PrePersist
    public void prePersist() {
        criadoEm = LocalDateTime.now();
    }
}
