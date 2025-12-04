package com.nexsol.tpa.core.domain;

import java.time.LocalDate;

public record AccidentHistory(
        LocalDate accidentDate,
        Long insurancePayment,
        String accidentContent
) {
}
