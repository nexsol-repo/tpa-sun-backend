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
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenIssuer implements TokenIssuer {

	private final JwtProperties jwtProperties;
    private final Key key;

    public JwtTokenIssuer(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

	@Override
	public AuthToken issue(Long userId, String email) {
        Instant now = Instant.now();


        String accessToken = createToken(userId, email, now, jwtProperties.getAccessTokenExpiration());

        String refreshToken = createToken(userId, email, now, jwtProperties.getRefreshTokenExpiration());
        return AuthToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiration(jwtProperties.getAccessTokenExpiration())
                .refreshTokenExpiration(jwtProperties.getRefreshTokenExpiration())
                .build();
	}

	private String createToken(Long userId, String email, Instant now, long expirationSeconds) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(this.key, SignatureAlgorithm.HS512)
                .compact();
    }


}
