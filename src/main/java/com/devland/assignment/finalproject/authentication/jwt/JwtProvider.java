package com.devland.assignment.finalproject.authentication.jwt;

import com.devland.assignment.finalproject.authentication.model.UserPrincipal;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${simple.auth.jwt.secret}")
    private String jwtSecret;

    @Value("${simple.auth.jwt.expiration}")
    private int jwtExpiration;

    public String generateJwtToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Map<String, Object> userInformation = new HashMap<>();
        userInformation.put("userId", userPrincipal.getId());
        userInformation.put("username", userPrincipal.getUsername());
        userInformation.put("name", userPrincipal.getName());

        return Jwts.builder()
                .claims(userInformation)
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + (this.jwtExpiration * 1000L)))
                .signWith(this.generateKey()).compact();
    }

    private SecretKey generateKey() {
        byte[] bytes = this.jwtSecret.getBytes(StandardCharsets.UTF_8);

        return Keys.hmacShaKeyFor(bytes);
    }

    public boolean isJwtTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(this.generateKey()).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException e) {
            logger.error("Invalid JWT Signature -> Message: {0}", e);
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT Token -> Message: {0}", e);
        } catch (ExpiredJwtException e) {
            logger.info("Expired JWT Token -> Message: {0}", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT Token -> Message: {0}", e);
        } catch (IllegalArgumentException e) {
            logger.error("JWT Claims String is Empty -> Message: {0}", e);
        }
        return false;
    }

    public String getUsernameFromJwtToken(String jwt) {
        return Jwts.parser()
                .verifyWith(this.generateKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();
    }
}