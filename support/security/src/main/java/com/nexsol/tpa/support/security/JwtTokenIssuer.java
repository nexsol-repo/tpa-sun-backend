package com.nexsol.tpa.support.security;

import com.nexsol.tpa.core.domain.AuthToken;
import com.nexsol.tpa.core.domain.TokenIssuer;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenIssuer implements TokenIssuer {

    private final JwtProperties jwtProperties;

    @Override
    public AuthToken issue(Long userId, String email) {
        long now = System.currentTimeMillis();

        String accessToken = createToken(userId, email, now, jwtProperties.getAccessTokenExpiration());

        String refreshToken = createToken(userId, email, now, jwtProperties.getRefreshTokenExpiration());
        return AuthToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiration(jwtProperties.getAccessTokenExpiration())
                .refreshTokenExpiration(jwtProperties.getRefreshTokenExpiration())
                .build();
    }

    private String createToken(Long userId, String email, long now, long expirationSeconds) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + (expirationSeconds * 1000)))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
}
