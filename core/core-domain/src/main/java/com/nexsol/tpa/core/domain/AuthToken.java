package com.nexsol.tpa.core.domain;

import lombok.Builder;

@Builder
public record AuthToken(
        String accessToken,
        String refreshToken,
        long accessTokenExpiration,
        long refreshTokenExpiration
) {
}
