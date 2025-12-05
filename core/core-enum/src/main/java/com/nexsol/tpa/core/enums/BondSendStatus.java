package com.nexsol.tpa.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BondSendStatus {

	NOT_SENT("미송부"), SENT("송부완료"), NOT_APPLICABLE("대상아님");

	private final String description;

}
