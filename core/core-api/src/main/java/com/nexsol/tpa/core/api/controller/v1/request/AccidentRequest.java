package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.Accident;

import java.time.LocalDate;

public record AccidentRequest(LocalDate date, Long paymentAmount, String content) {
	public Accident toAccident() {
		return Accident.builder().date(date).paymentAmount(paymentAmount).content(content).build();

	}
}
