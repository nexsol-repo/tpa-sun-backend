package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record Accident(LocalDate date, Long paymentAmount, String content) {
}
