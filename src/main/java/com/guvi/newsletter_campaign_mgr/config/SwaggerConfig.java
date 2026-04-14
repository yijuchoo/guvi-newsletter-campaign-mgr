package com.guvi.newsletter_campaign_mgr.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Paste your JWT token here. " +
                                                "Obtain it from /api/auth/login or /api/auth/register")
                        )
                );
    }

    private Info apiInfo() {
        return new Info()
                .title("Newsletter & Email Campaign Manager API")
                .version("1.0.0")
                .description("API for managing mailing lists, subscribers, and email campaigns. " +
                        "Authenticate via /api/auth/login to get a JWT token, " +
                        "then click 'Authorize' and paste the token.")
                .contact(new Contact()
                        .name("Newsletter Campaign Manager")
                        .email("admin@emailmanager.com")
                );
    }
}
