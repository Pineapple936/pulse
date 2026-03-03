package com.pulse.service;

import com.pulse.entity.User;
import com.pulse.entity.dto.auth.JWTAuthentificationDto;
import com.pulse.entity.dto.auth.LoginDto;
import com.pulse.entity.dto.auth.RefreshTokenDto;
import com.pulse.entity.dto.auth.RegisterDto;
import com.pulse.repository.UserRepository;
import com.pulse.security.JwtCore;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.util.HashSet;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final JwtCore jwtCore;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;

    @Transactional
    public void save(RegisterDto dto) {
        log.info("Registering user with email={}", dto.email());
        User user = repository.save(new User(dto.name(), dto.email(), passwordEncoder.encode(dto.password())));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), user.getPassword(), new HashSet<>()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("User registered and authenticated email={}", user.getEmail());
    }

    public JWTAuthentificationDto signIn(LoginDto userCredentialsDto) throws AuthenticationException {
        log.info("Sign-in requested for email={}", userCredentialsDto.email());
        User user = findByCredentials(userCredentialsDto);
        log.info("Sign-in successful for email={}", user.getEmail());
        return jwtCore.createAuthToken(user.getEmail());
    }

    @Deprecated(forRemoval = false)
    public JWTAuthentificationDto singIn(LoginDto userCredentialsDto) throws AuthenticationException {
        return signIn(userCredentialsDto);
    }

    public JWTAuthentificationDto refreshToken(RefreshTokenDto refreshTokenDto) throws AuthenticationException {
        String refreshToken = refreshTokenDto.refreshToken();
        if (refreshToken != null && jwtCore.validateJwtToken(refreshToken)) {
            User user = findByEmail(jwtCore.getEmailFromToken(refreshToken));
            log.info("Refresh token accepted for email={}", user.getEmail());
            return jwtCore.createAuthToken(user.getEmail(), refreshToken);
        }
        log.warn("Refresh token rejected");
        throw new AuthenticationException("Invalid refresh token");
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("Resolving current user for email={}", email);
        return findByEmail(email);
    }

    @Transactional(readOnly = true)
    private User findByCredentials(LoginDto userCredentialsDto) throws AuthenticationException {
        Optional<User> optionalUser = repository.findByEmail(userCredentialsDto.email());
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            if (passwordEncoder.matches(userCredentialsDto.password(), user.getPassword())){
                return user;
            }
        }
        log.warn("Authentication failed for email={}", userCredentialsDto.email());
        throw new AuthenticationException("Email or password is not correct");
    }

    @Transactional(readOnly = true)
    private User findByEmail(String email) throws EntityNotFoundException {
        log.debug("Finding user by email={}", email);
        return repository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email " + email + " not found")
        );
    }
}
