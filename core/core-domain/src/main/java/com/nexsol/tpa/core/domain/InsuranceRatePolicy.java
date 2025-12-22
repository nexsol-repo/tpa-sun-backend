package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.RateType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InsuranceRatePolicy {

	private final InsuranceRateReader rateReader;

	// private final AreaKeyResolver areaKeyResolver;
	//
	// // 1. 기본료 조회 (면적 기반)
	// public long getBasePremium(double area) {
	// // 구간 Key 매핑 로직 (도메인 정책)
	// String areaKey = areaKeyResolver.resolveKey(area);
	//
	// // DB 조회
	// return rateReader.read(RateType.GL_BASE_PREMIUM, areaKey).longValue();
	// }

	public double getGlUnitPrice() {
		return rateReader.read(RateType.GL_CALC_CONFIG, "UNIT_PRICE").doubleValue();
	}

	public double getGlUnitArea() {
		return rateReader.read(RateType.GL_CALC_CONFIG, "UNIT_AREA").doubleValue();
	}

	public double getGlMinPremium() {
		return rateReader.read(RateType.GL_CALC_CONFIG, "MIN_PREMIUM").doubleValue();
	}

	public double getGlDiscountFactor() {
		return rateReader.read(RateType.GL_CALC_CONFIG, "DISCOUNT_RATE").doubleValue();
	}

	// 2. 가입금액 계수 조회
	public double getAmountCoefficient(long amount) {
		String amountKey = resolveAmountKey(amount);
		return rateReader.read(RateType.GL_FACTOR_AMOUNT, amountKey).doubleValue();
	}

	// 3. 설비 형태 계수 조회
	public double getFacilityTypeCoefficient(String facilityType) {
		String typeKey = (facilityType != null && facilityType.contains("지붕")) ? "ROOF" : "LAND";
		return rateReader.read(RateType.GL_FACTOR_TYPE, typeKey).doubleValue();
	}

	// 4. 지역별 요율 조회 (MD/BI)
	public double getMdRate(String region) {
		return rateReader.read(RateType.MD_RATE_REGION, resolveRegionKey(region)).doubleValue();
	}

	public double getBiRate(String region) {
		return rateReader.read(RateType.BI_RATE_REGION, resolveRegionKey(region)).doubleValue();
	}

	private String resolveAmountKey(long amount) {
		long amountInHundredMillions = amount / 100_000_000;
		return String.valueOf(amountInHundredMillions);
	}

	private String resolveRegionKey(String region) {
		if (region == null)
			return "ETC";
		if (region.contains("전라") || region.contains("경상"))
			return "JEONLA_GYEONGSANG";
		if (region.contains("제주"))
			return "JEJU";
		return "ETC";
	}

}
