package com.devdad.book_worms.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecurityUtils {

    public static String getUserId(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
						log.info("[LOGGER] JWT FOUND ::{}", jwt);

            String sub = jwt.getSubject();
						log.info("[LOGGER] JWT SUB ID ::{}", sub);
            if (sub != null) return sub;

            String preferredUsername = jwt.getClaimAsString("preferred_username");
						log.info("[LOGGER] JWT Preferred Username Fallback ::{}", preferredUsername);
            if (preferredUsername != null) return preferredUsername;

        }
        return authentication.getName();
    }

}
