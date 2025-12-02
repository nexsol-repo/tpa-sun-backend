package com.nexsol.tpa.core.domain;

public interface TokenIssuer {

    AuthToken issue(Long userId, String email);
}
