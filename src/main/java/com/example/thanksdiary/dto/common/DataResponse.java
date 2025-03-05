package com.example.thanksdiary.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DataResponse<T> extends SuccessResponse {

	private final T response;

	public DataResponse(int code, String message, T response) {
		super(code, message);
		this.response = response;
	}

}
