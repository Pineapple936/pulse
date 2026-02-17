package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.dto.JWTAuthentificationDto;
import com.example.demo.entity.dto.LoginDto;
import com.example.demo.entity.dto.RefreshTokenDto;
import com.example.demo.entity.dto.RegisterDto;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtCore;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService extends CrudService<User, UserRepository, Long> {
    private final JwtCore jwtCore;
    private final PasswordEncoder passwordEncoder;

    public User save(RegisterDto dto) {
        User user = super.save(new User(dto.name(), dto.email(), passwordEncoder.encode(dto.password())));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), user.getPassword(), new HashSet<>()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return user;
    }

    public JWTAuthentificationDto singIn(LoginDto userCredentialsDto) throws AuthenticationException {
        User user = findByCredentials(userCredentialsDto);
        return jwtCore.createAuthToken(user.getEmail());
    }

    public JWTAuthentificationDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception {
        String refreshToken = refreshTokenDto.refreshToken();
        if (refreshToken != null && jwtCore.validateJwtToken(refreshToken)) {
            User user = findByEmail(jwtCore.getEmailFromToken(refreshToken));
            return jwtCore.createAuthToken(user.getEmail(), refreshToken);
        }
        throw new  AuthenticationException("Invalid refresh token");
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return findByEmail(email);
    }

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

    private User findByEmail(String email) throws EntityNotFoundException {
        return repository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email " + email + " not found")
        );
    }
}
