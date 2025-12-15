package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AccidentInfo(String accidentType, LocalDateTime accidentDate, String accidentPlace,
		String damageDescription, Long estimatedLossAmount, String accountBank, String accountNumber,
		String accountHolder) {
}