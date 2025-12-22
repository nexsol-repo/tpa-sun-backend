package com.nexsol.tpa.core.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class InsurancePremiumCalculator {

	private final InsuranceRatePolicy ratePolicy;

	public PremiumQuote calculate(InsurancePlant plant, InsuranceCondition condition) {

		// 1. 요율 조회 (Policy 위임)
		String region = plant.region();
		double mdRate = ratePolicy.getMdRate(region);
		double biRate = ratePolicy.getBiRate(region);

		// 2. 계산
		long premiumMD = calculatePremium(condition.propertyDamageAmount(), mdRate);
		long premiumBI = calculatePremium(condition.businessInterruptionAmount(), biRate);
		long premiumGL = calculateGlPremium(plant, condition.liabilityAmount());

		long totalPremium = premiumMD + premiumBI + premiumGL;

		return PremiumQuote.builder()
			.mdPremium(premiumMD)
			.biPremium(premiumBI)
			.glPremium(premiumGL)
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

		// 1. 면적 보정 로직 (기존 유지)
		BigDecimal area = plant.area();
		if (area == null || area.compareTo(BigDecimal.ZERO) == 0) {
			BigDecimal capacity = plant.capacity() != null ? plant.capacity() : BigDecimal.ZERO;
			area = capacity.multiply(BigDecimal.valueOf(4.93));
		}

		// 2. DB에서 요율 및 설정값 조회 (하드코딩 방지)
		double unitPrice = ratePolicy.getGlUnitPrice();    // 36,000
		double unitArea = ratePolicy.getGlUnitArea();      // 500
		double minPremium = ratePolicy.getGlMinPremium();  // 60,000
		double discountFactor = ratePolicy.getGlDiscountFactor(); // 0.85

		double amountFactor = ratePolicy.getAmountCoefficient(liabilityAmount);
		double typeFactor = ratePolicy.getFacilityTypeCoefficient(plant.facilityType());

		// 3. 엑셀 수식 적용: ((면적/500) * 36000) * 계수들 * 0.85
		double calculatedBase = (area.doubleValue() / unitArea) * unitPrice;
		double finalAmount = calculatedBase * amountFactor * typeFactor * discountFactor;

		// 4. 최저 보험료 적용 및 반올림 (십원 단위 반올림하여 백원 단위 표시)
		long premium = (long) Math.max(minPremium, finalAmount);
		return Math.round(premium / 100.0) * 100;
	}

}
