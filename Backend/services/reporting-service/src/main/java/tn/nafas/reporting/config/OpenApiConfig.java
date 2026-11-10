package tn.nafas.reporting.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI reportingOpenApi() {
        return new OpenAPI().info(new Info()
                .title("NAFAS - Reporting Service API")
                .version("0.0.1")
                .description("Citizens submit environmental observations; administrators review and manage them. "
                        + "Roles are simulated with the X-User-Role header (CITIZEN or ADMIN) until the "
                        + "User Management Service and the API Gateway provide real authentication."));
    }
}
