package com.nexsol.tpa.core.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoreErrorType {

	NOT_FOUND_DATA(CoreErrorKind.SERVER_ERROR, CoreErrorCode.C1000, "해당 데이터를 찾지 못했습니다.", CoreErrorLevel.INFO),
	// Auth User
	USER_NOT_FOUND(CoreErrorKind.SERVER_ERROR, CoreErrorCode.C1001, "해당 유저를 찾을 수 없습니다..", CoreErrorLevel.INFO),

	USER_EXIST_DATA(CoreErrorKind.CLIENT_ERROR, CoreErrorCode.C1003, "해당 유저가 존재합니다.", CoreErrorLevel.INFO),
    INVALID_INPUT(CoreErrorKind.CLIENT_ERROR, CoreErrorCode.C1004, "잘못된 입력값입니다.", CoreErrorLevel.INFO);


	private final CoreErrorKind kind;

	private final CoreErrorCode code;

	private final String message;

	private final CoreErrorLevel level;

}
