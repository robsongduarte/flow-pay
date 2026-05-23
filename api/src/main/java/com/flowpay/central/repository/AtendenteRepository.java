package com.flowpay.central.repository;

import com.flowpay.central.entity.Atendente;
import com.flowpay.central.enums.TimeAtendimento;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AtendenteRepository extends JpaRepository<Atendente, Long> {

    List<Atendente> findAllByOrderByCriadoEmAsc();

    long countByTimeAtendimentoAndAtivoTrue(TimeAtendimento timeAtendimento);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select a
            from Atendente a
            where a.timeAtendimento = :time
              and a.ativo = true
            order by a.id
            """)
    List<Atendente> findAtivosByTimeForUpdate(@Param("time") TimeAtendimento time);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Atendente a where a.id = :id")
    Optional<Atendente> findByIdForUpdate(@Param("id") Long id);
}
