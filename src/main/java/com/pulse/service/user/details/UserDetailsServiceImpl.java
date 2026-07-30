package com.pulse.service.user.details;

import com.pulse.repository.user.UserRepository;
import com.pulse.repository.user.entity.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        log.debug("Load user details by email={}", email);
        return userRepository.findByEmail(email).map(item -> new UserDetailsImpl(
                item.getId(), item.getName(), item.getEmail(), item.getPassword(), item.getCreatedAt()
        )).orElseThrow(
                () -> new UsernameNotFoundException("User with email " + email + " not found")
        );
    }
}
