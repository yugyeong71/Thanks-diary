package com.example.thanksdiary.common.exception;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.thanksdiary.dto.common.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * RequestBody Validation 에러
	 * HttpStatus 400
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
	public ErrorResponse validationException(MethodArgumentNotValidException e) {
		BindingResult bindingResult = e.getBindingResult();

		FieldError fieldError = null;

		// @NotBlank, @NotNull, @Size, @Pattern 어노테이션 순으로 유효성 검사 체크
		List<String> errorCase = List.of("NotBlank", "NotNull", "Size", "Pattern");

		for (String errorCode : errorCase) {
			fieldError = bindingResult.getFieldErrors().stream()
				.filter(error -> Objects.equals(error.getCode(), errorCode))
				.findFirst()
				.orElse(null);

			if (fieldError != null) {
				break;
			}
		}

		String message = (fieldError != null) ? fieldError.getDefaultMessage() : bindingResult.getFieldError().getField() + "의 형식이 올바르지 않습니다.";

		return ErrorResponse.of(HttpStatus.EXPECTATION_FAILED, message);
	}


	/**
	 * 이미 존재하는 데이터
	 * HttpStatus 422
	 */
	@ExceptionHandler(AlreadyDataException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ErrorResponse alreadyDataException(AlreadyDataException e) {
		return ErrorResponse.of(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
	}


}
