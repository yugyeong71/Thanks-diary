package com.example.thanksdiary.controller.user;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.thanksdiary.dto.common.DataResponse;
import com.example.thanksdiary.dto.user.request.UserLoginRequest;
import com.example.thanksdiary.dto.user.request.UserSignUpRequest;
import com.example.thanksdiary.dto.user.response.AccessTokenRefreshResponse;
import com.example.thanksdiary.dto.user.response.UserLoginResponse;
import com.example.thanksdiary.dto.user.response.UserSignUpResponse;
import com.example.thanksdiary.service.user.UserAuthService;

import jakarta.servlet.http.HttpServletRequest;
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

	@PostMapping(value = "/login", name = "사용자 로그인")
	public DataResponse<UserLoginResponse> userLogin(@RequestBody @Valid UserLoginRequest userLoginRequest) {
		return new DataResponse<>(userAuthService.userLogin(userLoginRequest));
	}

	@PutMapping(value = "/refresh-token", name = "AccessToken 재발급")
	public DataResponse<AccessTokenRefreshResponse> accessTokenRefresh(HttpServletRequest httpServletRequest) {
		return new DataResponse<>(userAuthService.accessTokenRefresh(httpServletRequest));
	}

}
