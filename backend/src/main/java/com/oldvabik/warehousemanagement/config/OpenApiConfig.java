package com.oldvabik.warehousemanagement.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("InternetShop API")
                        .version("1.0")
                        .description("API documentation for InternetShop"))
                .externalDocs(new ExternalDocumentation()
                        .description("GitHub repository")
                        .url("https://github.com/oldvabik/InternetShop"));
    }

}
