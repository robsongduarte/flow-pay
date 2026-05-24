package com.flowpay.central.repository;

import com.flowpay.central.entity.Atendimento;
import com.flowpay.central.enums.StatusAtendimento;
import com.flowpay.central.enums.TimeAtendimento;
import com.flowpay.central.repository.projection.AtendenteCargaProjection;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    List<Atendimento> findAllByOrderByCriadoEmDesc();
    List<Atendimento> findByStatusOrderByCriadoEmDesc(StatusAtendimento status);
    List<Atendimento> findByTimeAtendimentoOrderByCriadoEmDesc(TimeAtendimento timeAtendimento);
    List<Atendimento> findByStatusAndTimeAtendimentoOrderByCriadoEmDesc(
            StatusAtendimento status,
            TimeAtendimento timeAtendimento
    );

    long countByStatus(StatusAtendimento status);

    long countByStatusAndTimeAtendimento(StatusAtendimento status, TimeAtendimento timeAtendimento);

    long countByAtendenteIdAndStatus(Long atendenteId, StatusAtendimento status);

    @Query("""
            select at.atendente.id as atendenteId, count(at.id) as totalAtivos
            from Atendimento at
            where at.atendente.id in :atendenteIds
              and at.status = :status
            group by at.atendente.id
            """)
    List<AtendenteCargaProjection> countAtivosByAtendenteIdsAndStatus(
            @Param("atendenteIds") List<Long> atendenteIds,
            @Param("status") StatusAtendimento status
    );

    @Query("""
            select a.id as atendenteId, count(at.id) as totalAtivos
            from Atendente a
            left join Atendimento at
              on at.atendente = a
             and at.status = com.flowpay.central.enums.StatusAtendimento.EM_ATENDIMENTO
            where a.timeAtendimento = :time
              and a.ativo = true
            group by a.id
            """)
    List<AtendenteCargaProjection> findCargaAtivaPorTime(@Param("time") TimeAtendimento time);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select at from Atendimento at where at.id = :id")
    Optional<Atendimento> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Atendimento> findByTimeAtendimentoAndStatusAndAtendenteIsNullOrderByCriadoEmAscIdAsc(
            TimeAtendimento timeAtendimento,
            StatusAtendimento status,
            Pageable pageable
    );
}
