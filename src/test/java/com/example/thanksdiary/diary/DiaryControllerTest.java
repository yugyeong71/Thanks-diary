package com.example.thanksdiary.diary;

import static com.example.thanksdiary.common.ApiDocumentUtils.*;
import static com.example.thanksdiary.common.DocumentOptionalGenerator.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

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
import com.example.thanksdiary.controller.diary.DiaryController;
import com.example.thanksdiary.dto.diary.request.SimpleDiaryCreateRequest;
import com.example.thanksdiary.dto.diary.response.SimpleDiaryCreateResponse;
import com.example.thanksdiary.service.diary.DiaryService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(DiaryController.class)
public class DiaryControllerTest extends ControllerTest {

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
	private DiaryService diaryService;

	@Test
	@DisplayName("일기 작성 - 간단한")
	public void createSimpleDiary() throws Exception {

		// given
		SimpleDiaryCreateRequest simpleDiaryCreateRequest = SimpleDiaryCreateRequest.builder()
			.content("햇살이 창문을 따뜻하게 비추고, 부드러운 바람이 커튼을 살며시 흔든다.")
			.build();

		SimpleDiaryCreateResponse simpleDiaryCreateResponse = SimpleDiaryCreateResponse.builder()
			.id(1L)
			.content("햇살이 창문을 따뜻하게 비추고, 부드러운 바람이 커튼을 살며시 흔든다.")
			.createdAt(LocalDateTime.now())
			.build();

		given(diaryService.createSimpleDiary(any(HttpServletRequest.class), any(SimpleDiaryCreateRequest.class))).willReturn(simpleDiaryCreateResponse);

		// when
		ResultActions resultActions = mockMvc.perform(
			RestDocumentationRequestBuilders.post("/diary/simple")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(simpleDiaryCreateRequest))
				.header(HttpHeaders.AUTHORIZATION, "감사일기.Access.Token")
				.with(user("user").roles("USER"))
		);

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("code").value(200))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andDo(document("diary/simple/create",
				getDocumentRequest(),
				getDocumentResponse(),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
					fieldWithPath("response").type(JsonFieldType.OBJECT).description("결과 데이터"),
					fieldWithPath("response.id").type(JsonFieldType.NUMBER).description("일기 고유 번호"),
					fieldWithPath("response.content").type(JsonFieldType.STRING).description("일기 내용"),
					fieldWithPath("response.createdAt").type(JsonFieldType.STRING).attributes(localDateTimeFormat()).description("일기 작성일")
				)
			));

		verify(diaryService).createSimpleDiary(any(HttpServletRequest.class), any(SimpleDiaryCreateRequest.class));
	}

}
