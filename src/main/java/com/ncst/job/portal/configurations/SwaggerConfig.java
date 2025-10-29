package com.ncst.job.portal.configurations;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
     OpenAPI jobPortalOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Job Portal REST API")
                        .description("""
                                Backend for Job Portal System with Spring Boot, JWT, and Swagger.
                                <br/><br/>
                                👉 <a href="https://github.com/NikStack20/Job-Portal-System/blob/feature/README.md" target="_blank">
                                Project Repository (GitHub)</a>
                                <br/>
                                📧 <b>Contact:</b> <a href="mailto:codingnik20@gmail.com">codingnik20@gmail.com</a>
                                <br/><br/>
                                Licensed under <a href="https://www.apache.org/licenses/LICENSE-2.0" target="_blank">
                                Apache License 2.0</a>
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("NikStack (Developer)")
                                .email("codingnik20@gmail.com")
                                .url("https://github.com/NikStack20/Job-Portal-System"))
                        .license(new License()
                                .name("Apache License 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0"))
                )
                // 🔐 JWT Security Config
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}


