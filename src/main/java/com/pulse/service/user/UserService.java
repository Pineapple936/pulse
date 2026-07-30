package com.pulse.service.user;

import com.pulse.controller.auth.entity.request.RegisterUserRequest;
import com.pulse.jooq.tables.records.UserRecord;
import com.pulse.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repo;

    public UserRecord save(RegisterUserRequest req) {
        UserRecord record = new UserRecord();
        record.setName(req.name());
        if (req.gender() != null) {
            record.setGender(req.gender().name());
        }
        record.setAge(req.age());
        record.setWeight(req.weight());
        record.setHeight(req.height());
        record.setEmail(req.email());
        record.setPassword(passwordEncoder.encode(req.password()));
        record = repo.save(record);

        log.info("User registered and authenticated email={}", record.getEmail());
        return record;
    }

    public UserRecord findByEmail(String email) throws NoSuchElementException {
        log.debug("Finding user by email={}", email);
        return repo.findByEmail(email).orElseThrow(
                () -> new NoSuchElementException("User with email " + email + " not found")
        );
    }

    public UserRecord findByCredentials(String email, String password) throws AuthenticationException {
        UserRecord record = findByEmail(email);

        if (!passwordEncoder.matches(password, record.getPassword())){
            log.warn("Authentication failed for email={}", email);
            throw new AuthenticationException("Password is not correct");
        }

        return record;
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
