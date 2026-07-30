package com.pulse.service.security;

import com.pulse.repository.user.entity.UserDetailsImpl;
import com.pulse.service.user.details.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    private static final String EMAIL = "john@example.com";

    @Mock
    private JwtCore jwtCore;
    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private JwtFilter filter;

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    private Authentication currentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Test
    void validToken_setsAuthentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer good-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = org.mockito.Mockito.mock(FilterChain.class);

        UserDetailsImpl user = new UserDetailsImpl(1L, "John", EMAIL, "hash", null);
        when(jwtCore.validateJwtToken("good-token")).thenReturn(true);
        when(jwtCore.getEmailFromToken("good-token")).thenReturn(EMAIL);
        when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(user);

        filter.doFilter(request, response, chain);

        assertThat(currentAuthentication()).isNotNull();
        assertThat(currentAuthentication().getPrincipal()).isSameAs(user);
        verify(chain).doFilter(request, response);
    }

    @Test
    void noToken_doesNotAuthenticate() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = org.mockito.Mockito.mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertThat(currentAuthentication()).isNull();
        verify(chain).doFilter(request, response);
    }

    @Test
    void invalidToken_doesNotAuthenticate() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer bad-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = org.mockito.Mockito.mock(FilterChain.class);

        when(jwtCore.validateJwtToken("bad-token")).thenReturn(false);

        filter.doFilter(request, response, chain);

        assertThat(currentAuthentication()).isNull();
        verify(chain).doFilter(request, response);
    }

    @Test
    void nonBearerHeader_isIgnored() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNz");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = org.mockito.Mockito.mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertThat(currentAuthentication()).isNull();
        verify(chain).doFilter(request, response);
    }
}
