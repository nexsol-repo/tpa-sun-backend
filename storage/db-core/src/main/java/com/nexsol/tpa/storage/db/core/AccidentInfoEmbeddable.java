package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.AccidentInfo;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@NoArgsConstructor
public class AccidentInfoEmbeddable {
    private String accidentType;
    private LocalDateTime accidentDate;
    private String accidentPlace;
    private String damageDescription;
    private Long estimatedLossAmount;

    public AccidentInfoEmbeddable(AccidentInfo info) {
        if (info == null) return;
        this.accidentType = info.accidentType();
        this.accidentDate = info.accidentDate();
        this.accidentPlace = info.accidentPlace();
        this.damageDescription = info.damageDescription();
        this.estimatedLossAmount = info.estimatedLossAmount();
    }

    public AccidentInfo toDomain() {
        return AccidentInfo.builder()
                .accidentType(accidentType)
                .accidentDate(accidentDate)
                .accidentPlace(accidentPlace)
                .damageDescription(damageDescription)
                .estimatedLossAmount(estimatedLossAmount)
                .build();
    }
}
