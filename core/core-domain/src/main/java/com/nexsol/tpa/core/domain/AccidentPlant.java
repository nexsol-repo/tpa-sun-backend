package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccidentPlant(String name, String address, BigDecimal capacity, BigDecimal area) {
}
