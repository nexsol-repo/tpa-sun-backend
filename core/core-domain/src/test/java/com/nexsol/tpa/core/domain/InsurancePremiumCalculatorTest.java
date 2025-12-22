package com.nexsol.tpa.core.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class InsurancePremiumCalculatorTest {

	@InjectMocks
	private InsurancePremiumCalculator calculator;

	@Mock
	private InsuranceRatePolicy ratePolicy;

	@Test
	@DisplayName("GL 보험료 계산 검증: (지붕형, 1000m^2,1억)")
	void calculateGlPremium() {
		// given
		InsurancePlant plant = createPlant(new BigDecimal("1000"), "지붕위(판넬)");
		InsuranceCondition condition = createCondition(100_000_000L);

		// Mocking 설정 (엑셀 정책 기준)
		given(ratePolicy.getGlUnitPrice()).willReturn(36000.0);
		given(ratePolicy.getGlUnitArea()).willReturn(500.0);
		given(ratePolicy.getGlMinPremium()).willReturn(60000.0);
		given(ratePolicy.getGlDiscountFactor()).willReturn(0.85);
		given(ratePolicy.getAmountCoefficient(100_000_000L)).willReturn(1.0);
		given(ratePolicy.getFacilityTypeCoefficient("지붕위(판넬)")).willReturn(1.2);

		// when
		PremiumQuote quote = calculator.calculate(plant, condition);

		// then
		// 계산식: ((1000 / 500) * 36000) * 1.0 * 1.2 * 0.85 = 73,440
		// 반올림: 73,400
		assertThat(quote.glPremium()).isEqualTo(73_400L);
	}

	@Test
	@DisplayName("GL 보험료 계산 검증: (최저보험료 적용 케이스)")
	void calculateGlPremium_MinPremium() {
		// given
		InsurancePlant plant = createPlant(new BigDecimal("700"), "농지");
		InsuranceCondition condition = createCondition(500_000_000L);

		given(ratePolicy.getGlUnitPrice()).willReturn(36000.0);
		given(ratePolicy.getGlUnitArea()).willReturn(500.0);
		given(ratePolicy.getGlMinPremium()).willReturn(60000.0);
		given(ratePolicy.getGlDiscountFactor()).willReturn(0.85);
		given(ratePolicy.getAmountCoefficient(500_000_000L)).willReturn(1.2);
		given(ratePolicy.getFacilityTypeCoefficient("농지")).willReturn(1.0);

		// when
		PremiumQuote quote = calculator.calculate(plant, condition);

		// then
		// 계산식: ((700 / 500) * 36000) * 1.2 * 1.0 * 0.85 = 51,408
		// 결과: MAX(60000, 51408) -> 60,000
		assertThat(quote.glPremium()).isEqualTo(60_000L);
	}

	@Test
	@DisplayName("면적 미입력 시 용량 기반 역산 검증 (용량 1000kW -> 면적 4930m^2)")
	void calculateGlPremium_AreaCorrection() {
		// given
		InsurancePlant plant = InsurancePlant.builder()
			.area(BigDecimal.ZERO)
			.capacity(new BigDecimal("1000"))
			.facilityType("평지")
			.build();
		InsuranceCondition condition = createCondition(100_000_000L);

		given(ratePolicy.getGlUnitPrice()).willReturn(36000.0);
		given(ratePolicy.getGlUnitArea()).willReturn(500.0);
		given(ratePolicy.getGlMinPremium()).willReturn(60000.0);
		given(ratePolicy.getGlDiscountFactor()).willReturn(0.85);
		given(ratePolicy.getAmountCoefficient(100_000_000L)).willReturn(1.0);
		given(ratePolicy.getFacilityTypeCoefficient("평지")).willReturn(1.0);

		// when
		PremiumQuote quote = calculator.calculate(plant, condition);

		// then
		// 면적 역산: 1000 * 4.93 = 4,930
		// 계산식: ((4930 / 500) * 36000) * 1.0 * 1.0 * 0.85 = 301,716
		// 반올림: 301,700
		assertThat(quote.glPremium()).isEqualTo(301_700L);
	}

	@Test
	@DisplayName("최종 보험료 산출 검증: (제주, MD+BI+GL 합산)")
	void calculateTotalPremium_Example3() {
		// [Given] 엑셀 예시 3번 데이터 설정
		// 지역: 제주, 면적: 1700, 설비형태: 야산(나대지)
		InsurancePlant plant = InsurancePlant.builder()
			.region("제주")
			.area(new BigDecimal("1700"))
			.facilityType("야산")
			.build();

		// 가입금액: MD 3억, BI 2천만, GL 3억
		InsuranceCondition condition = InsuranceCondition.builder()
			.propertyDamageAmount(300_000_000L)
			.businessInterruptionAmount(20_000_000L)
			.liabilityAmount(300_000_000L)
			.build();

		// [Mocking] DB 요율 설정 (사용자가 제공한 DB 데이터 및 엑셀 기준)
		given(ratePolicy.getMdRate("제주")).willReturn(0.00170); // DB ID 5 [cite: 2]
		given(ratePolicy.getBiRate("제주")).willReturn(0.00270); // DB ID 6 [cite: 2]

		// GL 기초 설정값
		given(ratePolicy.getGlUnitPrice()).willReturn(36000.0);
		given(ratePolicy.getGlUnitArea()).willReturn(500.0);
		given(ratePolicy.getGlMinPremium()).willReturn(60000.0);
		given(ratePolicy.getGlDiscountFactor()).willReturn(0.85);

		// GL 계수 (3억: 1.15, 나대지: 1.0)
		given(ratePolicy.getAmountCoefficient(300_000_000L)).willReturn(1.15);
		given(ratePolicy.getFacilityTypeCoefficient("야산")).willReturn(1.0);

		// [When]
		PremiumQuote quote = calculator.calculate(plant, condition);

		// [Then] 각 항목별 및 최종 합계 검증

		// 1. MD 검증: 300,000,000 * 0.00170 = 510,000
		assertThat(quote.mdPremium()).isEqualTo(510_000L);

		// 2. BI 검증: 20,000,000 * 0.00270 = 54,000
		assertThat(quote.biPremium()).isEqualTo(54_000L);

		// 3. GL 검증: ((1700 / 500) * 36000) * 1.15 * 1.0 * 0.85 = 119,646 -> 반올림 119,600
		assertThat(quote.glPremium()).isEqualTo(119_600L);

		// 4. 최종 합계 검증: 510,000 + 54,000 + 119,600 = 683,600
		assertThat(quote.totalPremium()).isEqualTo(683_600L);
	}

	@Test
	@DisplayName("최종 보험료 산출 검증: (전라, BI 미선택)")
	void calculateTotalPremium_Example1() {
		// [Given] 엑셀 예시 1번 데이터
		InsurancePlant plant = InsurancePlant.builder()
			.region("전라")
			.area(new BigDecimal("1000"))
			.facilityType("평지")
			.build();

		InsuranceCondition condition = InsuranceCondition.builder()
			.propertyDamageAmount(100_000_000L)
			.businessInterruptionAmount(null) // BI 미선택
			.liabilityAmount(100_000_000L)
			.build();

		given(ratePolicy.getMdRate("전라")).willReturn(0.00150); // DB ID 3 [cite: 2]
		given(ratePolicy.getBiRate("전라")).willReturn(0.00260); // DB ID 4 [cite: 2]

		given(ratePolicy.getGlUnitPrice()).willReturn(36000.0);
		given(ratePolicy.getGlUnitArea()).willReturn(500.0);
		given(ratePolicy.getGlMinPremium()).willReturn(60000.0);
		given(ratePolicy.getGlDiscountFactor()).willReturn(0.85);

		given(ratePolicy.getAmountCoefficient(100_000_000L)).willReturn(1.0);
		given(ratePolicy.getFacilityTypeCoefficient("평지")).willReturn(1.0);

		// [When]
		PremiumQuote quote = calculator.calculate(plant, condition);

		// [Then]
		// MD: 100M * 0.0015 = 150,000
		// BI: 0
		// GL: ((1000 / 500) * 36000) * 1.0 * 1.0 * 0.85 = 61,200
		// Total: 150,000 + 61,200 = 211,200
		assertThat(quote.totalPremium()).isEqualTo(211_200L);
	}

	private InsurancePlant createPlant(BigDecimal area, String type) {
		return InsurancePlant.builder().area(area).facilityType(type).region("전라").build();
	}

	private InsuranceCondition createCondition(Long glAmount) {
		return InsuranceCondition.builder()
			.liabilityAmount(glAmount)
			.propertyDamageAmount(0L) // MD/BI는 0으로 세팅하여 GL만 집중 검증
			.businessInterruptionAmount(0L)
			.build();
	}

}
