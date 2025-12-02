package com.nexsol.tpa.core.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoreErrorType {

	NOT_FOUND_DATA(CoreErrorKind.SERVER_ERROR, CoreErrorCode.T1000, "해당 데이터를 찾지 못했습니다.", CoreErrorLevel.INFO),
	// Auth User
	USER_NOT_FOUND(CoreErrorKind.SERVER_ERROR, CoreErrorCode.T1001, "해당 유저를 찾을 수 없습니다..", CoreErrorLevel.INFO),

	USER_EXIST_DATA(CoreErrorKind.CLIENT_ERROR, CoreErrorCode.T1003, "해당 유저가 존재합니다.", CoreErrorLevel.INFO),
    INVALID_INPUT(CoreErrorKind.CLIENT_ERROR, CoreErrorCode.T1004, "잘못된 입력값입니다.", CoreErrorLevel.INFO),


    //EmailVerification
    EMAIL_VERIFIED_OVERTIME(CoreErrorKind.SERVER_ERROR,CoreErrorCode.T2001,"인증 시간이 만료되었습니다.",CoreErrorLevel.INFO),
    EMAIL_VERIFIED_INVALID(CoreErrorKind.SERVER_ERROR,CoreErrorCode.T2002,"인증 코드가 일치하지 않습니다.",CoreErrorLevel.INFO),
    EMAIL_VERIFIED_REPEAT(CoreErrorKind.CLIENT_ERROR,CoreErrorCode.T2003,"잠시 후 다시 시도해주세요.",CoreErrorLevel.INFO),
    EMAIL_VERIFIED_AUTH(CoreErrorKind.SERVER_ERROR,CoreErrorCode.T2004,"이메일 인증이 완료되지 않았습니다.",CoreErrorLevel.INFO),
    EMAIL_VERIFIED_EMPTY(CoreErrorKind.CLIENT_ERROR,CoreErrorCode.T2005,"인증 요청 내역이 없습니다.",CoreErrorLevel.INFO);
	private final CoreErrorKind kind;

	private final CoreErrorCode code;

	private final String message;

	private final CoreErrorLevel level;

}
