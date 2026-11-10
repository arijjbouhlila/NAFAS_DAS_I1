
package com.NAFAS.analytics_service.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalyticsController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello! Gabes EcoWatch Analytics Service is running!";
    }
}
