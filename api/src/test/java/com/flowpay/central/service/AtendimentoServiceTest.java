package com.flowpay.central.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flowpay.central.dto.atendimento.AtendimentoCreateRequest;
import com.flowpay.central.dto.atendimento.AtendimentoResponse;
import com.flowpay.central.entity.Atendente;
import com.flowpay.central.entity.Atendimento;
import com.flowpay.central.enums.AssuntoAtendimento;
import com.flowpay.central.enums.StatusAtendimento;
import com.flowpay.central.enums.TimeAtendimento;
import com.flowpay.central.repository.AtendenteRepository;
import com.flowpay.central.repository.AtendimentoRepository;
import com.flowpay.central.repository.projection.AtendenteCargaProjection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class AtendimentoServiceTest {

    @Mock
    private AtendimentoRepository atendimentoRepository;

    @Mock
    private AtendenteRepository atendenteRepository;

    @Mock
    private DashboardStreamService dashboardStreamService;

    @InjectMocks
    private AtendimentoService atendimentoService;

    @BeforeEach
    void setUp() {
        when(atendimentoRepository.save(any(Atendimento.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("1. Deve atribuir atendimento quando houver atendente disponivel")
    void deveAtribuirAtendimentoQuandoHouverAtendenteDisponivel() {
        Atendente atendente = buildAtendente(1L, "Ana", TimeAtendimento.CARTOES, true);
        when(atendenteRepository.findAtivosByTimeForUpdate(TimeAtendimento.CARTOES))
                .thenReturn(List.of(atendente));
        when(atendimentoRepository.countAtivosByAtendenteIdsAndStatus(
                List.of(1L),
                StatusAtendimento.EM_ATENDIMENTO
        )).thenReturn(List.of());

        AtendimentoResponse response = atendimentoService.criar(new AtendimentoCreateRequest(
                "Cliente 1",
                "123456789",
                AssuntoAtendimento.PROBLEMA_CARTAO
        ));

        assertThat(response.status()).isEqualTo(StatusAtendimento.EM_ATENDIMENTO);
        assertThat(response.atendenteId()).isEqualTo(1L);
        assertThat(response.timeAtendimento()).isEqualTo(TimeAtendimento.CARTOES);
        verify(dashboardStreamService, times(1)).publishDashboardUpdate();
    }

    @Test
    @DisplayName("2. Deve enfileirar quando todos os atendentes tiverem 3 atendimentos ativos")
    void deveEnfileirarQuandoTodosEstiveremOcupados() {
        Atendente atendenteA = buildAtendente(1L, "Ana", TimeAtendimento.CARTOES, true);
        Atendente atendenteB = buildAtendente(2L, "Bruno", TimeAtendimento.CARTOES, true);

        when(atendenteRepository.findAtivosByTimeForUpdate(TimeAtendimento.CARTOES))
                .thenReturn(List.of(atendenteA, atendenteB));
        when(atendimentoRepository.countAtivosByAtendenteIdsAndStatus(
                List.of(1L, 2L),
                StatusAtendimento.EM_ATENDIMENTO
        )).thenReturn(List.of(
                new CargaProjection(1L, 3L),
                new CargaProjection(2L, 3L)
        ));

        AtendimentoResponse response = atendimentoService.criar(new AtendimentoCreateRequest(
                "Cliente 2",
                "ABC12345",
                AssuntoAtendimento.PROBLEMA_CARTAO
        ));

        assertThat(response.status()).isEqualTo(StatusAtendimento.AGUARDANDO);
        assertThat(response.atendenteId()).isNull();
        verify(dashboardStreamService, times(1)).publishDashboardUpdate();
    }

    @Test
    @DisplayName("3. Deve atribuir proximo da fila quando um atendimento for finalizado")
    void deveAtribuirProximoDaFilaQuandoFinalizar() {
        Atendente atendente = buildAtendente(1L, "Ana", TimeAtendimento.CARTOES, true);

        Atendimento emAtendimento = buildAtendimento(
                10L,
                StatusAtendimento.EM_ATENDIMENTO,
                TimeAtendimento.CARTOES,
                AssuntoAtendimento.PROBLEMA_CARTAO,
                atendente
        );
        emAtendimento.setIniciadoEm(LocalDateTime.now().minusMinutes(10));

        Atendimento aguardando = buildAtendimento(
                11L,
                StatusAtendimento.AGUARDANDO,
                TimeAtendimento.CARTOES,
                AssuntoAtendimento.PROBLEMA_CARTAO,
                null
        );

        when(atendimentoRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(emAtendimento));
        when(atendenteRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(atendente));
        when(atendimentoRepository.countByAtendenteIdAndStatus(1L, StatusAtendimento.EM_ATENDIMENTO)).thenReturn(2L);
        when(atendimentoRepository.findByTimeAtendimentoAndStatusAndAtendenteIsNullOrderByCriadoEmAscIdAsc(
                eq(TimeAtendimento.CARTOES),
                eq(StatusAtendimento.AGUARDANDO),
                any(Pageable.class)
        )).thenReturn(List.of(aguardando));

        AtendimentoResponse response = atendimentoService.finalizar(10L);

        assertThat(response.status()).isEqualTo(StatusAtendimento.FINALIZADO);
        assertThat(aguardando.getStatus()).isEqualTo(StatusAtendimento.EM_ATENDIMENTO);
        assertThat(aguardando.getAtendente()).isNotNull();
        assertThat(aguardando.getAtendente().getId()).isEqualTo(1L);
        assertThat(aguardando.getIniciadoEm()).isNotNull();
        verify(dashboardStreamService, times(1)).publishDashboardUpdate();
    }

    @Test
    @DisplayName("4. Deve direcionar PROBLEMA_CARTAO para CARTOES")
    void deveDirecionarProblemaCartaoParaCartoes() {
        when(atendenteRepository.findAtivosByTimeForUpdate(TimeAtendimento.CARTOES)).thenReturn(List.of());

        AtendimentoResponse response = atendimentoService.criar(new AtendimentoCreateRequest(
                "Cliente 3",
                "DOC1000",
                AssuntoAtendimento.PROBLEMA_CARTAO
        ));

        assertThat(response.timeAtendimento()).isEqualTo(TimeAtendimento.CARTOES);
    }

    @Test
    @DisplayName("5. Deve direcionar CONTRATACAO_EMPRESTIMO para EMPRESTIMOS")
    void deveDirecionarContratacaoEmprestimoParaEmprestimos() {
        when(atendenteRepository.findAtivosByTimeForUpdate(TimeAtendimento.EMPRESTIMOS)).thenReturn(List.of());

        AtendimentoResponse response = atendimentoService.criar(new AtendimentoCreateRequest(
                "Cliente 4",
                "DOC2000",
                AssuntoAtendimento.CONTRATACAO_EMPRESTIMO
        ));

        assertThat(response.timeAtendimento()).isEqualTo(TimeAtendimento.EMPRESTIMOS);
    }

    @Test
    @DisplayName("6. Deve direcionar OUTROS para OUTROS_ASSUNTOS")
    void deveDirecionarOutrosParaOutrosAssuntos() {
        when(atendenteRepository.findAtivosByTimeForUpdate(TimeAtendimento.OUTROS_ASSUNTOS)).thenReturn(List.of());

        AtendimentoResponse response = atendimentoService.criar(new AtendimentoCreateRequest(
                "Cliente 5",
                "DOC3000",
                AssuntoAtendimento.OUTROS
        ));

        assertThat(response.timeAtendimento()).isEqualTo(TimeAtendimento.OUTROS_ASSUNTOS);
    }

    private Atendente buildAtendente(Long id, String nome, TimeAtendimento time, boolean ativo) {
        Atendente atendente = Atendente.builder()
                .id(id)
                .nome(nome)
                .timeAtendimento(time)
                .ativo(ativo)
                .criadoEm(LocalDateTime.now())
                .build();
        return atendente;
    }

    private Atendimento buildAtendimento(
            Long id,
            StatusAtendimento status,
            TimeAtendimento time,
            AssuntoAtendimento assunto,
            Atendente atendente
    ) {
        Atendimento atendimento = Atendimento.builder()
                .id(id)
                .nomeCliente("Cliente")
                .documentoCliente("DOC")
                .assunto(assunto)
                .timeAtendimento(time)
                .status(status)
                .atendente(atendente)
                .criadoEm(LocalDateTime.now().minusMinutes(5))
                .build();
        return atendimento;
    }

    private record CargaProjection(Long atendenteId, long totalAtivos) implements AtendenteCargaProjection {
        @Override
        public Long getAtendenteId() {
            return atendenteId;
        }

        @Override
        public long getTotalAtivos() {
            return totalAtivos;
        }
    }
}
