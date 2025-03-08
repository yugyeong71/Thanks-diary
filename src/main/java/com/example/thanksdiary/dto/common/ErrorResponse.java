package com.example.thanksdiary.dto.common;

import java.io.IOException;

import org.springframework.http.HttpStatus;

import com.example.thanksdiary.common.util.JsonUtil;

import jakarta.servlet.http.HttpServletResponse;
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

	public static void of(HttpServletResponse response, HttpStatus httpStatus, String message) throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.getWriter().write(JsonUtil.convertObjectToJson(ErrorResponse.of(httpStatus, message)));
	}

}
