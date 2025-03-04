package com.example.thanksdiary.controller.user;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.thanksdiary.dto.common.SuccessResponse;
import com.example.thanksdiary.dto.user.request.EmailCheckRequest;
import com.example.thanksdiary.service.user.EmailService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

	private final EmailService emailService;

	@PostMapping(value = "/check", name = "이메일 중복 확인")
	public SuccessResponse emailCheck(@RequestBody @Valid EmailCheckRequest emailCheckRequest) {
		return emailService.emailCheck(emailCheckRequest);
	}
}
