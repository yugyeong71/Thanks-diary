package com.example.thanksdiary.controller.user;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.thanksdiary.dto.common.DataResponse;
import com.example.thanksdiary.dto.user.request.UserSignUpRequest;
import com.example.thanksdiary.dto.user.response.UserSignUpResponse;
import com.example.thanksdiary.service.user.UserAuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/auth")
public class UserAuthController {

	private final UserAuthService userAuthService;

	@PostMapping(value = "/signup", name = "사용자 회원가입")
	public DataResponse<UserSignUpResponse> userSignUp(@RequestBody @Valid UserSignUpRequest userSignUpRequest) {
		return new DataResponse<>(userAuthService.userSignUp(userSignUpRequest));
	}

}
