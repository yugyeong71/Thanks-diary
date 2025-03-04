package com.example.thanksdiary.dto.common;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

	private final int code;

	private final String message;

	public static ErrorResponse of(HttpStatus httpStatus, String msg) {
		return new ErrorResponse(httpStatus.value(), msg);
	}

}
