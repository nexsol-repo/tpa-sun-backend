package com.nexsol.tpa.core.domain;

import org.springframework.stereotype.Component;

import java.util.NavigableMap;
import java.util.TreeMap;

@Component
public class AreaKeyResolver {
    private final NavigableMap<Double, String> areaKeyMap = new TreeMap<>();

    public AreaKeyResolver() {
        // [구간 정의] Key는 구간의 '상한값(미만 기준)' 또는 '대표값'
        // DB의 rate_key와 매핑될 문자열을 Value로 저장
        areaKeyMap.put(0.0, "0");
        areaKeyMap.put(750.0, "750");
        areaKeyMap.put(1250.0, "1250");
        areaKeyMap.put(1750.0, "1750");
        areaKeyMap.put(2250.0, "2250");
        areaKeyMap.put(2750.0, "2750");
        areaKeyMap.put(3250.0, "3250");
        areaKeyMap.put(3750.0, "3750");
        areaKeyMap.put(4250.0, "4250");
        areaKeyMap.put(4750.0, "4750");
    }

    public String resolveKey(double area) {
        // 입력된 area보다 작거나 같은 키 중 가장 큰 것 (floorKey) 찾기
        // 예: 1300 -> 1250.0 (Key) -> "1250" (Value)
        var entry = areaKeyMap.floorEntry(area);
        return entry != null ? entry.getValue() : "0";
    }
}
