package com.alberdev.study.appointmenthub.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI appointmentHubOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Appointment Hub API")
                        .version("v1")
                        .description("API REST para gerenciamento de clientes, profissionais, servicos e agendamentos.")
                        .contact(new Contact()
                                .name("Alberto Vilar")
                                .url("https://www.linkedin.com/in/albertovilar1/")));
    }
}
