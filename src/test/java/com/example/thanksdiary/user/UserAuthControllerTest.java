package com.example.thanksdiary.user;

import static com.example.thanksdiary.common.ApiDocumentUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.thanksdiary.common.ControllerTest;
import com.example.thanksdiary.controller.user.UserAuthController;
import com.example.thanksdiary.dto.user.request.UserLoginRequest;
import com.example.thanksdiary.dto.user.request.UserSignUpRequest;
import com.example.thanksdiary.dto.user.response.AccessTokenRefreshResponse;
import com.example.thanksdiary.dto.user.response.UserLoginResponse;
import com.example.thanksdiary.dto.user.response.UserSignUpResponse;
import com.example.thanksdiary.service.user.UserAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(UserAuthController.class)
public class UserAuthControllerTest extends ControllerTest {

	private MockMvc mockMvc;

	@BeforeEach
	void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.apply(documentationConfiguration(restDocumentation))
			.build();
	}

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private UserAuthService userAuthService;

	@MockitoBean
	private PasswordEncoder passwordEncoder;

	@Test
	@DisplayName("사용자 회원가입")
	public void userSignUp() throws Exception {

		// given
		String password = "pwd1234!";

		UserSignUpRequest userSignUpRequest = UserSignUpRequest.builder()
			.email("thanks123@gmail.com")
			.password(password)
			.code("123456")
			.build();

		UserSignUpResponse userSignUpResponse = UserSignUpResponse.builder()
			.id(1L)
			.email("thanks123@gmail.com")
			.accessToken("AccessToken")
			.refreshToken("RefreshToken")
			.build();

		given(userAuthService.userSignUp(any(UserSignUpRequest.class))).willReturn(userSignUpResponse);

		given(passwordEncoder.encode(any(String.class))).willReturn(password);

		// when
		ResultActions resultActions = mockMvc.perform(
			RestDocumentationRequestBuilders.post("/user/auth/signup")
				.content(objectMapper.writeValueAsString(userSignUpRequest))
				.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("code").value(200))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andDo(document("user/auth/signup",
				getDocumentRequest(),
				getDocumentResponse(),
				requestFields(
					fieldWithPath("email").type(JsonFieldType.STRING).description("사용자 이메일"),
					fieldWithPath("password").type(JsonFieldType.STRING).description("사용자 비밀번호"),
					fieldWithPath("code").type(JsonFieldType.STRING).description("사용자 가입 인증 코드")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
					fieldWithPath("response").type(JsonFieldType.OBJECT).description("결과 데이터"),
					fieldWithPath("response.id").type(JsonFieldType.NUMBER).description("사용자 고유 번호"),
					fieldWithPath("response.email").type(JsonFieldType.STRING).description("사용자 이메일"),
					fieldWithPath("response.accessToken").type(JsonFieldType.STRING).description("Access Token"),
					fieldWithPath("response.refreshToken").type(JsonFieldType.STRING).description("Refresh Token")
				)
			));

		verify(userAuthService).userSignUp(any(UserSignUpRequest.class));
	}

	@Test
	@DisplayName("사용자 로그인")
	public void userLogin() throws Exception {

		// given
		String password = "pwd1234!";

		UserLoginRequest userLoginRequest = UserLoginRequest.builder()
			.email("thanks123@gmail.com")
			.password(password)
			.build();

		UserLoginResponse userLoginResponse = UserLoginResponse.builder()
			.id(1L)
			.email("thanks123@gmail.com")
			.accessToken("AccessToken")
			.refreshToken("RefreshToken")
			.build();

		given(userAuthService.userLogin(any(UserLoginRequest.class))).willReturn(userLoginResponse);

		given(passwordEncoder.encode(any(String.class))).willReturn(password);

		// when
		ResultActions resultActions = mockMvc.perform(
			RestDocumentationRequestBuilders.post("/user/auth/login")
				.content(objectMapper.writeValueAsString(userLoginRequest))
				.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("code").value(200))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andDo(document("user/auth/login",
				getDocumentRequest(),
				getDocumentResponse(),
				requestFields(
					fieldWithPath("email").type(JsonFieldType.STRING).description("사용자 이메일"),
					fieldWithPath("password").type(JsonFieldType.STRING).description("사용자 비밀번호")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
					fieldWithPath("response").type(JsonFieldType.OBJECT).description("결과 데이터"),
					fieldWithPath("response.id").type(JsonFieldType.NUMBER).description("사용자 고유 번호"),
					fieldWithPath("response.email").type(JsonFieldType.STRING).description("사용자 이메일"),
					fieldWithPath("response.accessToken").type(JsonFieldType.STRING).description("Access Token"),
					fieldWithPath("response.refreshToken").type(JsonFieldType.STRING).description("Refresh Token")
				)
			));

		verify(userAuthService).userLogin(any(UserLoginRequest.class));
	}

	@Test
	@DisplayName("AccessToken 재발급")
	public void accessTokenRefresh() throws Exception {

		// given
		AccessTokenRefreshResponse accessTokenRefreshResponse = AccessTokenRefreshResponse.builder()
			.accessToken("AccessToken")
			.build();

		given(userAuthService.accessTokenRefresh(any(HttpServletRequest.class))).willReturn(accessTokenRefreshResponse);

		// when
		ResultActions result = mockMvc.perform(
			RestDocumentationRequestBuilders.put("/user/auth/refresh-token")
				.header("refreshToken", "감사일기.Refresh.Token")
				.accept(MediaType.APPLICATION_JSON)
				.with(user("user").roles("USER"))
		);

		// then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("code").value(200))
			.andDo(document("user/auth/refresh-token",
				getDocumentRequest(),
				getDocumentResponse(),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
					fieldWithPath("response").type(JsonFieldType.OBJECT).description("결과 데이터"),
					fieldWithPath("response.accessToken").type(JsonFieldType.STRING).description("Access Token")
				)
			));
	}
}
