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
@RequiredArgsConstructor
public class UserService {
    private final JwtCore jwtCore;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;

    @Transactional
    public void save(RegisterDto dto) {
        User user = repository.save(new User(dto.name(), dto.email(), passwordEncoder.encode(dto.password())));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), user.getPassword(), new HashSet<>()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public JWTAuthentificationDto singIn(LoginDto userCredentialsDto) throws AuthenticationException {
        User user = findByCredentials(userCredentialsDto);
        return jwtCore.createAuthToken(user.getEmail());
    }

    public JWTAuthentificationDto refreshToken(RefreshTokenDto refreshTokenDto) throws AuthenticationException {
        String refreshToken = refreshTokenDto.refreshToken();
        if (refreshToken != null && jwtCore.validateJwtToken(refreshToken)) {
            User user = findByEmail(jwtCore.getEmailFromToken(refreshToken));
            return jwtCore.createAuthToken(user.getEmail(), refreshToken);
        }
        throw new AuthenticationException("Invalid refresh token");
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
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
        throw new AuthenticationException("Email or password is not correct");
    }

    @Transactional(readOnly = true)
    private User findByEmail(String email) throws EntityNotFoundException {
        return repository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email " + email + " not found")
        );
    }
}
