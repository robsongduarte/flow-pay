package com.flowpay.central.config;

import com.flowpay.central.entity.Atendente;
import com.flowpay.central.enums.TimeAtendimento;
import com.flowpay.central.repository.AtendenteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final AtendenteRepository atendenteRepository;

    @Bean
    CommandLineRunner initData() {
        return args -> {
            if (atendenteRepository.count() > 0) {
                return;
            }

            List<Atendente> atendentesIniciais = List.of(
                    Atendente.builder().nome("Ana Cartoes").timeAtendimento(TimeAtendimento.CARTOES).ativo(true).build(),
                    Atendente.builder().nome("Bruno Cartoes").timeAtendimento(TimeAtendimento.CARTOES).ativo(true).build(),
                    Atendente.builder().nome("Carla Emprestimos").timeAtendimento(TimeAtendimento.EMPRESTIMOS).ativo(true).build(),
                    Atendente.builder().nome("Diego Emprestimos").timeAtendimento(TimeAtendimento.EMPRESTIMOS).ativo(true).build(),
                    Atendente.builder().nome("Eva Outros").timeAtendimento(TimeAtendimento.OUTROS_ASSUNTOS).ativo(true).build(),
                    Atendente.builder().nome("Fabio Outros").timeAtendimento(TimeAtendimento.OUTROS_ASSUNTOS).ativo(true).build()
            );

            atendenteRepository.saveAll(atendentesIniciais);
        };
    }
}
