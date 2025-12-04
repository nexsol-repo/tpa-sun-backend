package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.enums.EmailVerifiedType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmailVerifyRequest(@Email @NotBlank String email, @NotBlank String code,
		@NotNull EmailVerifiedType type) {
}
