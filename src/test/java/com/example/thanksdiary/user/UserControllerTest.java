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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.thanksdiary.common.ControllerTest;
import com.example.thanksdiary.controller.user.UserController;
import com.example.thanksdiary.dto.user.response.UserAutoLoginResponse;
import com.example.thanksdiary.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(UserController.class)
public class UserControllerTest extends ControllerTest {

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
	private UserService userService;

	@Test
	@DisplayName("사용자 회원탈퇴")
	public void userRevoke() throws Exception {

		// given
		doNothing().when(userService).userRevoke(any(HttpServletRequest.class));

		// when
		ResultActions resultActions = mockMvc.perform(
			RestDocumentationRequestBuilders.delete("/user")
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "감사일기.Access.Token")
				.with(user("user").roles("USER"))
		);

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("code").value(200))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andDo(document("user/revoke",
				getDocumentRequest(),
				getDocumentResponse(),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지")
				)
			));

		verify(userService).userRevoke(any(HttpServletRequest.class));
	}

	@Test
	@DisplayName("사용자 자동 로그인")
	public void userAutoLogin() throws Exception {

		// given
		UserAutoLoginResponse userAutoLoginResponse = UserAutoLoginResponse.builder()
			.id(1L)
			.email("thanks123@gmail.com")
			.accessToken("AccessToken")
			.refreshToken("RefreshToken")
			.build();

		given(userService.userAutoLogin(any(HttpServletRequest.class))).willReturn(userAutoLoginResponse);

		// when
		ResultActions resultActions = mockMvc.perform(
			RestDocumentationRequestBuilders.put("/user/auto-login")
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "감사일기.Access.Token")
				.with(user("user").roles("USER"))
		);

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("code").value(200))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andDo(document("user/auto-login",
				getDocumentRequest(),
				getDocumentResponse(),
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

		verify(userService).userAutoLogin(any(HttpServletRequest.class));
	}

}
