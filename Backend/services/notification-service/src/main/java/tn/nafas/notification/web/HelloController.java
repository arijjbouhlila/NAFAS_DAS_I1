package tn.nafas.notification.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "Service", description = "Basic liveness endpoint")
public class HelloController {
    @Operation(summary = "Hello endpoint (session 4 validation)")
    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("service", "notification-service",
                "stack", "Java / Spring Boot",
                "message", "Hello from the NAFAS Notification Service!");
    }
}