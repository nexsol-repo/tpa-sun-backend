package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.AccidentInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AccidentInfoRequest(
        @NotBlank(message = "사고 종류는 필수입니다.")
        String accidentType,

        @NotNull(message = "사고 일시는 필수입니다.")
        LocalDateTime accidentDate,

        @NotBlank(message = "사고 장소는 필수입니다.")
        String accidentPlace,

        @NotBlank(message = "피해 내용은 필수입니다.")
        String damageDescription,
        Long estimatedLossAmount
) {
    public AccidentInfo toAccidentInfo() {
        return AccidentInfo.builder()
                .accidentType(accidentType)
                .accidentDate(accidentDate)
                .accidentPlace(accidentPlace)
                .damageDescription(damageDescription)
                .estimatedLossAmount(estimatedLossAmount)
                .build();
    }
}
