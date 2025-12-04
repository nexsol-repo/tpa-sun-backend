package com.nexsol.tpa.core.domain;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class InsurancePremiumCalculator {

    public InsuranceCoverage calculate(InsurancePlant plant,InsuranceCondition condition){
        // 1-1. 지역별 요율 조회 (엑셀 Sheet1 참조)
        RateTable rate = getRateByRegion(plant.addressInfo().region());

        long premiumMD = calculateMdPremium(condition.propertyDamageAmount(), rate.mdRate);
        long premiumBI = calculateBiPremium(condition.businessInterruptionAmount(), rate.biRate);

        // 1-2. 배상책임(GL) 보험료 계산
        long premiumGL = calculateGlPremium(plant, condition.liabilityAmount());

        long totalPremium = premiumMD + premiumBI + premiumGL;

        // 자기부담금 등 설정 (고정값 예시)
        return InsuranceCoverage.builder()
                .propertyDamageDeductible(100_000_000L) // 예시
                .liabilityDeductible(300_000L)
                .businessInterruptionDeductible(0L)
                .totalPremium(totalPremium)
                .build();
    }

    private long calculateMdPremium(Long amount, double rate) {
        if (amount == null) return 0;
        return (long) (amount * rate);
    }

    private long calculateBiPremium(Long amount, double rate) {
        if (amount == null) return 0;
        return (long) (amount * rate);
    }

    // GL 보험료 = 기본료(면적) * 가입금액계수 * 설비형태계수
    private long calculateGlPremium(InsurancePlant plant, Long liabilityAmount) {
        if (liabilityAmount == null) return 0;

        // 1. 면적 산출 (입력값 없으면 용량 * 4.93)
        BigDecimal area = plant.area();
        if (area == null || area.compareTo(BigDecimal.ZERO) == 0) {
            area = plant.capacity().multiply(BigDecimal.valueOf(4.93));
        }

        long basePremium = getGlBasePremium(area.doubleValue());
        double liabilityAmountFactor = getGlAmountCoefficient(liabilityAmount);
        double facilityTypeFactor = getGlTypeCoefficient(plant.facilityType());

        return (long) (basePremium * liabilityAmountFactor * facilityTypeFactor);
    }

    // --- Lookup Tables (Private) ---

    private record RateTable(double mdRate, double biRate) {}

    private RateTable getRateByRegion(String region) {
        if (region == null) return new RateTable(0.0015, 0.0024); // 기본 (그 외)

        if (region.contains("전라") || region.contains("경상")) {
            return new RateTable(0.0015, 0.0026);
        } else if (region.contains("제주")) {
            return new RateTable(0.0017, 0.0027);
        } else {
            return new RateTable(0.0015, 0.0024); // 그 외 지역
        }
    }

    // GL 기본료 (면적 구간별)
    private long getGlBasePremium(double area) {
        if (area < 750) return 60_000;
        if (area < 1250) return 60_000;
        if (area < 1750) return 76_000;
        if (area < 2250) return 101_000;
        if (area < 2750) return 126_000;
        if (area < 3250) return 152_000;
        if (area < 3750) return 177_000;
        if (area < 4250) return 202_000;
        if (area < 4750) return 227_000;
        return 252_000; // 4750 이상 (최대치 적용)
    }

    // GL 가입금액 계수
    private double getGlAmountCoefficient(long amount) {
        long amountInUk = amount / 100_000_000; // 억 단위
        if (amountInUk <= 1) return 1.0;
        if (amountInUk <= 2) return 1.1;
        if (amountInUk <= 3) return 1.15;
        if (amountInUk <= 5) return 1.2;
        if (amountInUk <= 10) return 1.4;
        return 1.5; // 10억 초과
    }

    // GL 설비형태 계수 (지붕형 1.2배)
    private double getGlTypeCoefficient(String facilityType) {
        if (facilityType != null && facilityType.contains("지붕")) {
            return 1.2;
        }
        return 1.0;
    }
}
