package com.system.wasimikuna.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI wasimikunaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Wasimikuna - Sistema de Gestión de Alimentos")
                        .description("API REST para el sistema de gestión de alimentos Wasimikuna. " +
                                   "Este sistema permite administrar usuarios, productos, órdenes de compra, " +
                                   "envíos, recepciones, programación de menús y más.")
                        .version("1.0.0"));
    }
}