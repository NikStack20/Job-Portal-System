package com.ncst.job.portal.configurations;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    OpenAPI jobPortalOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Job Portal REST API")
                        .description("Backend for Job Portal System with Spring Boot, JWT, and Swagger")
                        .version("1.0.0"));
    }
}

