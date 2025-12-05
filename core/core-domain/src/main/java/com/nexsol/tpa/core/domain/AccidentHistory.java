package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AccidentHistory(
        LocalDate accidentDate,
        Long insurancePayment,
        String accidentContent
) {
}
