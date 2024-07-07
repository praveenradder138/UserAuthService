package com.project.userauthservice.utils;


import com.project.userauthservice.exceptions.InvalidJwtTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {
    @Value("${security.jwt.secret}")
    private String secretKey;

    @Value("${security.jwt.token.expiration}")
    private Long jwtExpiration;

    public String extractUsername(String token) throws InvalidJwtTokenException {
        return extractClaim(token, Claims::getSubject);
    }

    public List<String> extractRoles(String token) throws InvalidJwtTokenException {
        return extractClaim(token, claims -> claims.get("roles", List.class));
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws InvalidJwtTokenException {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", userDetails.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .toList());
        return generateToken(extraClaims, userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) throws InvalidJwtTokenException {
        final String username = extractUsername(token);
        final List<String> roles = extractRoles(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token) &&
                roles.equals(userDetails.getAuthorities().stream()
                        .map(authority -> authority.getAuthority())
                        .toList());
    }

    private boolean isTokenExpired(String token) throws InvalidJwtTokenException {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) throws InvalidJwtTokenException {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) throws InvalidJwtTokenException {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new InvalidJwtTokenException("JWT Exception: "+e.getMessage());
        }
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
