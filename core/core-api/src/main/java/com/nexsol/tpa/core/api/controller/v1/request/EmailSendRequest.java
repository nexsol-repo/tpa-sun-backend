package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.enums.EmailVerifiedType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmailSendRequest(@NotBlank String companyCode, @Email @NotBlank String email,
		@NotNull EmailVerifiedType type) {
}
