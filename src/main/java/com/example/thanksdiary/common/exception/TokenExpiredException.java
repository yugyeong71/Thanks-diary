package com.example.thanksdiary.common.exception;

import com.example.thanksdiary.domain.common.enums.ResponseMessage;

public class TokenExpiredException extends RuntimeException {

	public TokenExpiredException() {
		super(ResponseMessage.FORBIDDEN_MESSAGE.getMessage());
	}

}
