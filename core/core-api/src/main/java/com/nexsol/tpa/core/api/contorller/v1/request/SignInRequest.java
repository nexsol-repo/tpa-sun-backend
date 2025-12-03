package com.nexsol.tpa.core.api.contorller.v1.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignInRequest(@NotBlank String companyCode, @Email @NotBlank String email, @NotBlank String code) {
}
