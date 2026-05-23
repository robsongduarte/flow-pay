package com.flowpay.central.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI flowPayOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("FlowPay API")
                        .description("API REST da central de relacionamento da FlowPay.")
                        .version("v1")
                        .contact(new Contact()
                                .name("FlowPay")
                                .email("suporte@flowpay.local"))
                        .license(new License()
                                .name("Uso Interno")
                                .url("https://flowpay.local")));
    }
}
