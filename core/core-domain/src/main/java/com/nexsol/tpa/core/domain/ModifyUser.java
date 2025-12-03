package com.nexsol.tpa.core.domain;

import lombok.Builder;

@Builder
public record ModifyUser(String email, String applicantName, String applicantEmail, String applicantPhoneNumber) {
}
