package com.benjamin.blog.security;

import com.benjamin.blog.exception.BadAPIException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private String jwtExpirationInMs;

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            throw new BadAPIException(HttpStatus.BAD_REQUEST, "Invalid JWT Signature.");
        } catch (MalformedJwtException e) {
            throw new BadAPIException(HttpStatus.BAD_REQUEST, "Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            throw new BadAPIException(HttpStatus.BAD_REQUEST, "Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            throw new BadAPIException(HttpStatus.BAD_REQUEST, "Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            throw new BadAPIException(HttpStatus.BAD_REQUEST, "JWT claim string is empty.");
        }
    }
}
