package com.nexsol.tpa.core.domain;

import lombok.Builder;

@Builder
public record AccidentInsured(String companyCode, String ceoName, String ceoPhone) {
}
