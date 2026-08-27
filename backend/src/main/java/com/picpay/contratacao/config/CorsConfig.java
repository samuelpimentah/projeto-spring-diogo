package com.picpay.contratacao.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 * CORS define quais páginas abertas em outro endereço podem chamar esta API.
 * A pasta do front-end pode se chamar webApp; o navegador considera apenas
 * a origem da página, formada por protocolo, domínio e porta.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /*
     * WebMvcConfigurer permite complementar a configuração padrão do Spring
     * MVC sem substituir o comportamento que o framework já fornece.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/funcionarios/**")
                .allowedOrigins(
                        "http://localhost:5173",
                        "http://127.0.0.1:5173"
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Accept")
                .maxAge(3600);
    }
}
