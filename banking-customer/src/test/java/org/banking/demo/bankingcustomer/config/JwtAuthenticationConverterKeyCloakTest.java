package org.banking.demo.bankingcustomer.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

class JwtAuthenticationConverterKeyCloakTest {

    private JwtAuthenticationConverterKeyCloak converter;

    private static final String CLIENT_ID = "spring-customerapp-client";
    private static final String PREFERRED_USERNAME_CLAIM = "preferred_username";

    @BeforeEach
    void setUp() {
        converter = new JwtAuthenticationConverterKeyCloak();
    }

    private Jwt createMockJwt(Map<String, Object> claims) {
        Jwt jwt = mock(Jwt.class);

        when(jwt.getHeaders()).thenReturn(Map.of("alg", "RS256"));
        when(jwt.getClaims()).thenReturn(claims);
        when(jwt.getIssuedAt()).thenReturn(Instant.now());
        when(jwt.getExpiresAt()).thenReturn(Instant.now().plusSeconds(3600));

        when(jwt.hasClaim(anyString())).thenAnswer(invocation ->
                claims.containsKey(invocation.getArgument(0))
        );
        when(jwt.getClaim(anyString())).thenAnswer(invocation ->
                claims.get(invocation.getArgument(0))
        );
        when(jwt.getClaimAsMap(anyString())).thenAnswer(invocation ->
                (Map<String, Object>) claims.get(invocation.getArgument(0))
        );

        return jwt;
    }


    @Test
    @DisplayName("Debe usar 'preferred_username' si está presente")
    void getPrincipleAuttributeName_ShouldUsePreferredUsername() {
        String expectedUsername = "juan.perez";
        Map<String, Object> claims = Map.of(
                JwtClaimNames.SUB, "user-id-123",
                PREFERRED_USERNAME_CLAIM, expectedUsername
        );
        Jwt jwt = createMockJwt(claims);

        JwtAuthenticationToken token = (JwtAuthenticationToken) converter.convert(jwt);

        assertEquals(expectedUsername, token.getName());
    }

    @Test
    @DisplayName("Debe usar 'sub' si 'preferred_username' no está presente")
    void getPrincipleAuttributeName_ShouldFallbackToSub() {
        String expectedSub = "sub";
        Map<String, Object> claims = Map.of(
                JwtClaimNames.SUB, expectedSub
        );
        Jwt jwt = createMockJwt(claims);

        JwtAuthenticationToken token = (JwtAuthenticationToken) converter.convert(jwt);

        assertEquals(expectedSub, token.getName());
    }


    @Test
    @DisplayName("Debe extraer y mapear roles correctamente con prefijo ROLE_")
    void getAuthoritiesFromRoles_ShouldExtractAndPrefixRoles() {

        List<String> rawRoles = List.of("admin_client_role", "user_client_role");
        Map<String, Object> resourceAccessClaims = Map.of(
                CLIENT_ID, Map.of("roles", rawRoles)
        );
        Map<String, Object> claims = Map.of(
                "resource_access", resourceAccessClaims,
                JwtClaimNames.SUB, "test"
        );
        Jwt jwt = createMockJwt(claims);

        JwtAuthenticationToken token = (JwtAuthenticationToken) converter.convert(jwt);
        List<? extends GrantedAuthority> authorities = (List<? extends GrantedAuthority>) token.getAuthorities();

        assertFalse(authorities.isEmpty());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_admin_client_role")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_user_client_role")));
        assertEquals(2, authorities.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"resource_access", CLIENT_ID, "roles"})
    @DisplayName("Debe retornar lista vacía si falta un claim de roles (e.g., resource_access, client_id, roles)")
    void getAuthoritiesFromRoles_ShouldReturnEmptyListIfClaimMissing(String missingPart) {

        Map<String, Object> claims;
        if (missingPart.equals("resource_access")) {
            claims = Map.of(JwtClaimNames.SUB, "test");
        } else if (missingPart.equals(CLIENT_ID)) {
            claims = Map.of(
                    "resource_access", Map.of("other-client", Map.of("roles", List.of("role"))),
                    JwtClaimNames.SUB, "test"
            );
        } else { // 'roles' missing
            claims = Map.of(
                    "resource_access", Map.of(CLIENT_ID, Map.of("other-key", "value")),
                    JwtClaimNames.SUB, "test"
            );
        }

        Jwt jwt = createMockJwt(claims);

        JwtAuthenticationToken token = (JwtAuthenticationToken) converter.convert(jwt);

        assertTrue(token.getAuthorities().isEmpty());
    }

    @Test
    @DisplayName("Debe manejar roles vacíos correctamente")
    void getAuthoritiesFromRoles_ShouldHandleEmptyRolesList() {

        Map<String, Object> resourceAccessClaims = Map.of(
                CLIENT_ID, Map.of("roles", Collections.emptyList())
        );
        Map<String, Object> claims = Map.of(
                "resource_access", resourceAccessClaims,
                JwtClaimNames.SUB, "test"
        );
        Jwt jwt = createMockJwt(claims);

        JwtAuthenticationToken token = (JwtAuthenticationToken) converter.convert(jwt);

        assertTrue(token.getAuthorities().isEmpty());
    }
}