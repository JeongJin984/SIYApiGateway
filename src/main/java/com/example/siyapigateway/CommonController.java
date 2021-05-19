package com.example.siyapigateway;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommonController {

    private final Environment env;

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in Gateway Service" +
                //", port(local.server.port)=" + env.getProperty("local.server.port") +
                ", port(server.port)=" + env.getProperty("server.port") +
                ", token secret(token.secret)=" + env.getProperty("token.secret") +
                ", expiration_time(token.expiration)=" + env.getProperty("token.expiration_time"));
    }
}
