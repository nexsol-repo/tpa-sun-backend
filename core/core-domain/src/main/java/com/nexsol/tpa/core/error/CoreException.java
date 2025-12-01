package com.nexsol.tpa.core.error;

import lombok.Getter;

@Getter
public class CoreException extends RuntimeException {

	private final CoreErrorType errorType;

	public CoreException(CoreErrorType errorType) {

		super(errorType.getMessage());
		this.errorType = errorType;
	}

}