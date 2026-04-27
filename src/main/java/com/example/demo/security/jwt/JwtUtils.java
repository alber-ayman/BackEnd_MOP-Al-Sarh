package com.example.demo.security.jwt;

import java.security.SignatureException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.example.demo.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${Marbella-Art.app.jwtSecret}")
    private String jwtSecret;

    @Value("${Marbella-Art.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String userName;

    public String generateJwtToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        userName = userPrincipal.getUsername();
        Map<String, Object> map = new HashMap<>();
        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();
        System.out.println("--------------->authorities" + authorities.size());
       
        map.put("roles", authorities);

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setHeaderParams(map)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
//                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
