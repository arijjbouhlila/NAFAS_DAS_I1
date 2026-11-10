package tn.esprit.nafas.userpreferences;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Nafas User Preferences", version = "0.0.1"))
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

@RestController
class HelloController {
    @GetMapping("/hello")
    @Operation(summary = "Identify the running service")
    public HelloResponse hello() {
        return new HelloResponse("user-preferences", "spring-boot", "Hello from User Preferences");
    }

    record HelloResponse(String service, String stack, String message) {}
}
