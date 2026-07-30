package com.pulse.controller.user;

import com.pulse.repository.user.entity.UserDetailsImpl;
import com.pulse.service.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService service;

    @DeleteMapping
    public ResponseEntity<Void> deleteById(@AuthenticationPrincipal UserDetailsImpl user) {
        service.deleteById(user.getId());
        return ResponseEntity.noContent().build();
    }
}
