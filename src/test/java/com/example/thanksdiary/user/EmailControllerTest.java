package com.example.thanksdiary.user;

import static com.example.thanksdiary.common.ApiDocumentUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.thanksdiary.common.ControllerTest;
import com.example.thanksdiary.controller.user.EmailController;
import com.example.thanksdiary.dto.common.SuccessResponse;
import com.example.thanksdiary.dto.user.request.SendVerificationCodeRequest;
import com.example.thanksdiary.dto.user.request.VerifyEmailCodeRequest;
import com.example.thanksdiary.service.user.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(EmailController.class)
public class EmailControllerTest extends ControllerTest {

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
	private EmailService emailService;

	@Test
	@DisplayName("이메일 중복 확인")
	public void emailCheck() throws Exception {

		// given
		doNothing().when(emailService).emailCheck(any(String.class));

		// when
		ResultActions resultActions = mockMvc.perform(
			RestDocumentationRequestBuilders.get("/email/check?email={email}", "thanks123@gmail.com")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("code").value(200))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andDo(document("user/email/check",
				getDocumentRequest(),
				getDocumentResponse(),
				queryParameters(
					parameterWithName("email").description("사용자 이메일")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지")
				)
			));

		verify(emailService).emailCheck(any(String.class));
	}

	@Test
	@DisplayName("이메일 인증 코드 요청")
	public void sendVerificationCode() throws Exception {

		// given
		SendVerificationCodeRequest sendVerificationCodeRequest = SendVerificationCodeRequest.builder()
			.email("thanks123@gmail.com")
			.build();

		given(emailService.sendVerificationCode(any(SendVerificationCodeRequest.class))).willReturn(new SuccessResponse());

		// when
		ResultActions resultActions = mockMvc.perform(
			RestDocumentationRequestBuilders.post("/email/send")
				.content(objectMapper.writeValueAsString(sendVerificationCodeRequest))
				.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("code").value(200))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andDo(document("user/email/send",
				getDocumentRequest(),
				getDocumentResponse(),
				requestFields(
					fieldWithPath("email").type(JsonFieldType.STRING).description("사용자 이메일")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지")
				)
			));

		verify(emailService).sendVerificationCode(any(SendVerificationCodeRequest.class));
	}

	@Test
	@DisplayName("이메일 인증 코드 검증")
	public void verifyEmailCode() throws Exception {

		// given
		VerifyEmailCodeRequest verifyEmailCodeRequest = VerifyEmailCodeRequest.builder()
			.email("thanks123@gmail.com")
			.code("123456")
			.build();

		given(emailService.verifyEmailCode(any(VerifyEmailCodeRequest.class))).willReturn(new SuccessResponse());

		// when
		ResultActions resultActions = mockMvc.perform(
			RestDocumentationRequestBuilders.post("/email/verify")
				.content(objectMapper.writeValueAsString(verifyEmailCodeRequest))
				.contentType(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("code").value(200))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andDo(document("user/email/verify",
				getDocumentRequest(),
				getDocumentResponse(),
				requestFields(
					fieldWithPath("email").type(JsonFieldType.STRING).description("사용자 이메일"),
					fieldWithPath("code").type(JsonFieldType.STRING).description("이메일 인증 코드")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지")
				)
			));

		verify(emailService).verifyEmailCode(any(VerifyEmailCodeRequest.class));
	}

}
