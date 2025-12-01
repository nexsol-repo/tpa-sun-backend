package com.nexsol.tpa.core.api.support.error;

import lombok.Getter;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

@Getter
public enum CoreApiErrorType {

	DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, CoreApiErrorCode.C500, "알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
			LogLevel.ERROR),
	INVALID_REQUEST(HttpStatus.BAD_REQUEST, CoreApiErrorCode.C400, "요청이 올바르지 않습니다.", LogLevel.INFO),
	NOT_FOUND_DATA(HttpStatus.BAD_REQUEST, CoreApiErrorCode.C401, "해당 데이터를 찾을 수 없습니다.", LogLevel.ERROR),

	AUTH_USER_NOT_FOUND(HttpStatus.BAD_REQUEST, CoreApiErrorCode.C4000, "유저를 찾을수 없습니다.", LogLevel.ERROR),
	AUTH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, CoreApiErrorCode.C4001, "비밀번호가 잘못되었습니다", LogLevel.ERROR);

	private final HttpStatus status;

	private final CoreApiErrorCode code;

	private final String message;

	private final LogLevel logLevel;

	CoreApiErrorType(HttpStatus status, CoreApiErrorCode code, String message, LogLevel logLevel) {
		this.status = status;
		this.code = code;
		this.message = message;
		this.logLevel = logLevel;
	}

}
