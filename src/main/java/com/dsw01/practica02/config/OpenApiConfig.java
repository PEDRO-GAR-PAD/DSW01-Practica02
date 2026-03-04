package com.dsw01.practica02.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI empleadosOpenApi() {
        SecurityScheme basicAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("basic");

        return new OpenAPI()
                .info(new Info()
                        .title("Empleados API")
                        .version("1.0.0")
                        .description("Contrato CRUD de empleados con autenticación básica."))
                .components(new Components().addSecuritySchemes("basicAuth", basicAuth))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
    }
}