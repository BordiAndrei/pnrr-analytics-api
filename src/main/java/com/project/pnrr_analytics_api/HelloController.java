package com.project.pnrr_analytics_api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String home() {
        return "Salut, Andrei! Aplicatia ta Spring Boot ruleaza pe Render cu Java 21!";
    }
}