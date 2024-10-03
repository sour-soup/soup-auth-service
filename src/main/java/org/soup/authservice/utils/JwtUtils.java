package org.soup.authservice.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.soup.authservice.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public final class JwtUtils {
    @Value("${security.jwt.secret}")
    private String salt;

    public String generateToken(User user, long expirationMillis) {
        Algorithm algorithm = Algorithm.HMAC256(salt);
        return JWT.create()
                .withIssuer("auth-service")
                .withSubject(user.username())
                .withClaim("id", user.id().toString())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationMillis))
                .sign(algorithm);
    }

    public boolean verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(salt);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(salt);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getSubject();
    }

    public UUID getIdFromToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(salt);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(token);

        return UUID.fromString(jwt.getClaim("id").asString());
    }
}
