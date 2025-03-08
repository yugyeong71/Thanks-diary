package com.example.thanksdiary.controller.user;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.thanksdiary.dto.common.SuccessResponse;
import com.example.thanksdiary.dto.user.request.SendVerificationCodeRequest;
import com.example.thanksdiary.dto.user.request.VerifyEmailCodeRequest;
import com.example.thanksdiary.service.user.EmailService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

	private final EmailService emailService;

	@GetMapping(value = "/check", name = "이메일 중복 확인")
	public SuccessResponse emailCheck(
		@NotBlank(message = "이메일은 필수입니다.")
		@Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$", message = "올바르지 않은 이메일입니다.")
		@RequestParam String email) {
		emailService.emailCheck(email);
		return new SuccessResponse();
	}

	@PostMapping(value = "/send", name = "이메일 인증 코드 요청")
	public SuccessResponse sendVerificationCode(@RequestBody @Valid SendVerificationCodeRequest sendVerificationCodeRequest) {
		return emailService.sendVerificationCode(sendVerificationCodeRequest);
	}

	@PostMapping(value = "/verify", name = "이메일 인증 코드 검증")
	public SuccessResponse verifyEmailCode(@RequestBody @Valid VerifyEmailCodeRequest verifyEmailCodeRequest) {
		return emailService.verifyEmailCode(verifyEmailCodeRequest);
	}
}
