package com.example.thanksdiary.common.exception;

import java.util.Objects;

public class NotFoundDataException extends RuntimeException {

	public NotFoundDataException(String message) {
		super(Objects.requireNonNull(message, "예외 메시지는 필수입니다."));
	}

}
