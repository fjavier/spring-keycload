package org.banking.demo.bankingcustomer.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtAuthenticationConverterKeyCloak implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public  AbstractAuthenticationToken convert(Jwt jwt) {
        return new JwtAuthenticationToken(jwt,getAuthoritiesFromRoles(jwt), getPrincipleAuttributeName(jwt));
    }

    private String getPrincipleAuttributeName(Jwt jwt) {
        String principleAttributeName = "preferred_username";
        if (jwt.hasClaim(principleAttributeName)) {
            return jwt.getClaim(principleAttributeName).toString();
        }

        return JwtClaimNames.SUB;
    }

    //Obtenemos la lista de roles de keycloak y lo mapeamos para que SpringSecurity lo entienda
    private List<? extends GrantedAuthority> getAuthoritiesFromRoles(Jwt jwt) {

        return Optional.ofNullable(jwt.getClaimAsMap("resource_access"))
                .map(resourceAccess -> (Map<String, Object>) resourceAccess.get("spring-customerapp-client"))
                .map(clientAccess -> (List<String>) clientAccess.get("roles"))
                .orElseGet(Collections::emptyList)
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }
}
