package com.example.thanksdiary.dto.common;

import com.example.thanksdiary.domain.common.enums.ResponseMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SuccessResponse {

	private final int code;

	private final String message;

	public SuccessResponse() {
		this.code = 200;
		this.message = ResponseMessage.SUCCESS_MESSAGE.getMessage();
	}

}
