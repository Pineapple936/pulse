package com.pulse.controller.ping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
public class PingController {
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        log.info("Ping server");
        return ResponseEntity.ok("pong");
    }
}
