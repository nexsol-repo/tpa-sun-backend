package com.nexsol.tpa.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InsuranceDocumentType {

	BUSINESS_LICENSE("사업자등록증", true), POWER_GEN_LICENSE("발전사업허가증", true), PRE_USE_INSPECTION("사용전검사확인증", false),
	SUPPLY_CERTIFICATE("공급인증서", false), ETC("기타", false), SIGNATURE("전자서명", true);
	;

	private final String description;

	private final boolean required;

}