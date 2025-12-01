package org.banking.demo.bankingcustomer.config;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@EnableWebSecurity
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationConverterKeyCloak jwtAuthenticationConverterKeyCloak;

    @InjectMocks
    private SecurityConfig securityConfig;

    SecurityConfigTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSecurityFilterChain_shouldConfigureHttpSecurityCorrectly() throws Exception {
        // Mock HttpSecurity object
        HttpSecurity httpSecurityMock = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        when(httpSecurityMock.csrf(any())).thenReturn(httpSecurityMock);
        when(httpSecurityMock.authorizeHttpRequests(any())).thenReturn(httpSecurityMock);
        when(httpSecurityMock.oauth2ResourceServer(any())).thenReturn(httpSecurityMock);
        when(httpSecurityMock.sessionManagement(any())).thenReturn(httpSecurityMock);

        SecurityFilterChain securityFilterChain = securityConfig.securityFilterChain(httpSecurityMock);

        assertNotNull(securityFilterChain, "SecurityFilterChain should not be null");
        verify(httpSecurityMock).csrf(any());
        verify(httpSecurityMock).authorizeHttpRequests(any());
        verify(httpSecurityMock).oauth2ResourceServer(any());
        verify(httpSecurityMock).sessionManagement(any());
    }
}