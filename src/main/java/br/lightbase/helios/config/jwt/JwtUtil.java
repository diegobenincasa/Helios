package br.lightbase.helios.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    
    private long expirationTime;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration.time}") long expirationTime) {
        this.algorithm = Algorithm.HMAC512(secret);
        this.verifier = JWT.require(algorithm).build(); // Removed issuer requirement
        this.expirationTime = expirationTime * 60 * 1000;
    }

    public String generateToken(String username) {
        return JWT.create()
            .withSubject(username)
            .withClaim("type", "access")
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
            .sign(algorithm);
    }

    public String generateRefreshToken(String username) {
        return JWT.create()
            .withSubject(username)
            .withClaim("type", "refresh")
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(LocalDate.now().plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC)*1000))
            .sign(algorithm);
    }

    public boolean validateToken(String token) {
        try {
            DecodedJWT jwt = verifier.verify(token);
            boolean exp = isTokenExpired(jwt);
            return !exp;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getSubject();
    }

    private boolean isTokenExpired(DecodedJWT jwt) {
        return jwt.getExpiresAt().before(new Date());
    }

    public boolean isRefreshToken(String token) {
        DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);
        return "refresh".equals(decodedJWT.getClaim("type").asString());
    }

    public boolean isAccessToken(String token) {
        DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);
        return "access".equals(decodedJWT.getClaim("type").asString());
    }
}