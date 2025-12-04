package com.nexsol.tpa.core.domain;

public record Address(
        String code,
        String addressLine,
        String addressDetail,
        String region
) {
}
