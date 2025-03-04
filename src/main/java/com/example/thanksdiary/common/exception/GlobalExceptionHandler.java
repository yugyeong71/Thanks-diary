package com.example.thanksdiary.common.exception;

import java.util.List;

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
	 * HttpStatus 417
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse validationException(MethodArgumentNotValidException e) {
		BindingResult bindingResult = e.getBindingResult();

		// @NotBlank, @NotNull, @Size, @Pattern 어노테이션 순으로 유효성 검사 체크
		List<String> errorCase = List.of("NotBlank", "NotNull", "Size", "Pattern");

		String message = bindingResult.getFieldErrors().stream()
			.filter(error -> errorCase.contains(error.getCode()))
			.findFirst()
			.map(FieldError::getDefaultMessage)
			.orElse("요청 값의 형식이 올바르지 않습니다.");

		return ErrorResponse.of(HttpStatus.BAD_REQUEST, message);
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
