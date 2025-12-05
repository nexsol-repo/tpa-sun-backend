package com.nexsol.tpa.core.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class InsurancePremiumCalculator {

	private final InsuranceRatePolicy ratePolicy;

	public InsuranceCoverage calculate(InsurancePlant plant, InsuranceCondition condition) {

		// 1. 요율 조회 (Policy 위임)
		String region = plant.addressInfo().region();
		double mdRate = ratePolicy.getMdRate(region);
		double biRate = ratePolicy.getBiRate(region);

		// 2. 계산
		long premiumMD = calculatePremium(condition.propertyDamageAmount(), mdRate);
		long premiumBI = calculatePremium(condition.businessInterruptionAmount(), biRate);
		long premiumGL = calculateGlPremium(plant, condition.liabilityAmount());

		long totalPremium = premiumMD + premiumBI + premiumGL;

		return InsuranceCoverage.builder()
			.propertyDamageDeductible(100_000_000L)
			.liabilityDeductible(300_000L)
			.businessInterruptionDeductible(0L)
			.totalPremium(totalPremium)
			.build();
	}

	private long calculatePremium(Long amount, double rate) {
		if (amount == null)
			return 0;
		return (long) (amount * rate);
	}

	private long calculateGlPremium(InsurancePlant plant, Long liabilityAmount) {
		if (liabilityAmount == null)
			return 0;

		// 면적 보정 로직
		BigDecimal area = plant.area();
		if (area == null || area.compareTo(BigDecimal.ZERO) == 0) {
			BigDecimal capacity = plant.capacity() != null ? plant.capacity() : BigDecimal.ZERO;
			area = capacity.multiply(BigDecimal.valueOf(4.93));
		}

		// Policy를 통해 값 조회 (구간 로직 등은 Policy가 알아서 처리)
		long basePremium = ratePolicy.getBasePremium(area.doubleValue());
		double amountFactor = ratePolicy.getAmountCoefficient(liabilityAmount);
		double typeFactor = ratePolicy.getFacilityTypeCoefficient(plant.facilityType());

		return (long) (basePremium * amountFactor * typeFactor);
	}

}
