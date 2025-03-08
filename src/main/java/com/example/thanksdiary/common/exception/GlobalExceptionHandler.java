package com.example.thanksdiary.common.exception;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import com.example.thanksdiary.domain.common.enums.ResponseMessage;
import com.example.thanksdiary.dto.common.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;

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
	 * 인증 에러
	 * HttpStatus 401
	 */
	@ExceptionHandler(UnauthorizedException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ErrorResponse UnauthorizedException(UnauthorizedException e) {
		return ErrorResponse.of(HttpStatus.UNAUTHORIZED, e.getMessage());
	}

	/**
	 * 권한 에러
	 * HttpStatus 403
	 */
	@ExceptionHandler(TokenExpiredException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorResponse TokenExpiredException(TokenExpiredException e) {
		return ErrorResponse.of(HttpStatus.FORBIDDEN, ResponseMessage.FORBIDDEN_MESSAGE.getMessage());
	}

	/**
	 * RequestParam Validation 에러
	 * HttpStatus 417
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
	public ErrorResponse MissingServletRequestParameterException(MissingServletRequestParameterException e) {
		return ErrorResponse.of(HttpStatus.EXPECTATION_FAILED, e.getParameterName());
	}

	/**
	 * multipart/form-data Validation 에러
	 * HttpStatus 417
	 */
	@ExceptionHandler(MissingServletRequestPartException.class)
	@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
	public ErrorResponse MissingServletRequestPartException(MissingServletRequestPartException e) {
		return ErrorResponse.of(HttpStatus.EXPECTATION_FAILED, e.getRequestPartName());
	}

	/**
	 * ModelAttribute Validation 에러
	 * HttpStatus 417
	 */
	@ExceptionHandler(BindException.class)
	@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
	public ErrorResponse BindException(BindException e) {
		return ErrorResponse.of(HttpStatus.EXPECTATION_FAILED, e.getFieldError().getField());
	}

	/**
	 * formValidation Validation 에러
	 * HttpStatus 417
	 */
	@ExceptionHandler(FormValidationException.class)
	@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
	public ErrorResponse FormValidationException(FormValidationException e) {
		return ErrorResponse.of(HttpStatus.EXPECTATION_FAILED, e.getMessage());
	}

	/**
	 * formValidation Validation 에러
	 * HttpStatus 417
	 */
	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
	public ErrorResponse ValidationException(HttpServletRequest request, ValidationException e) {
		return ErrorResponse.of(HttpStatus.EXPECTATION_FAILED, e.getMessage());
	}

	/**
	 * Validation 에러
	 * HttpStatus 417
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
	public ErrorResponse ConstraintViolationException(HttpServletRequest request, ConstraintViolationException e) {
		ConstraintViolation constraintViolation = null;
		// @NotBlank, @NotNull, @Size, @Pattern 어노테이션 순으로 유효성 검사 체크
		List<String> errorCase = List.of("NotBlank", "NotNull", "Size", "Pattern");

		for (String errorCode : errorCase) {
			constraintViolation = e.getConstraintViolations().stream()
				.filter(error -> Objects.equals(error.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(), errorCode))
				.findFirst()
				.orElse(null);

			if (constraintViolation != null) {
				break;
			}
		}

		String message = (constraintViolation != null) ? constraintViolation.getMessage() : e.getConstraintViolations().iterator().next().getMessage();

		return ErrorResponse.of(HttpStatus.EXPECTATION_FAILED, message);
	}

	/**
	 * 잘못된 요청
	 * HttpStatus 417
	 */
	@ExceptionHandler(BadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse BadRequestException(BadRequestException e) {
		String message = (e.getMessage() == null || e.getMessage().isEmpty())
			? ResponseMessage.BAD_REQUEST_MESSAGE.getMessage()
			: e.getMessage();

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
