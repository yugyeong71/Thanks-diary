package com.example.thanksdiary.controller.user;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.thanksdiary.dto.common.DataResponse;
import com.example.thanksdiary.dto.common.SuccessResponse;
import com.example.thanksdiary.dto.user.response.UserAutoLoginResponse;
import com.example.thanksdiary.service.user.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

	private final UserService userService;

	@DeleteMapping(name = "사용자 회원탈퇴")
	public SuccessResponse userRevoke(HttpServletRequest httpServletRequest) {
		userService.userRevoke(httpServletRequest);
		return new SuccessResponse();
	}

	@PutMapping(value = "/auto-login", name = "사용자 자동 로그인")
	public DataResponse<UserAutoLoginResponse> userAutoLogin(HttpServletRequest httpServletRequest){
		return new DataResponse<>(userService.userAutoLogin(httpServletRequest));
	}

}
