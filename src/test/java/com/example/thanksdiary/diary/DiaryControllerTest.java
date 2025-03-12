package com.example.thanksdiary.diary;

import static com.example.thanksdiary.common.ApiDocumentUtils.*;
import static com.example.thanksdiary.common.DocumentOptionalGenerator.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

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
import com.example.thanksdiary.dto.diary.request.DetailedDiaryCreateRequest;
import com.example.thanksdiary.dto.diary.request.SimpleDiaryCreateRequest;
import com.example.thanksdiary.dto.diary.response.DetailedDiaryCreateResponse;
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
			.date(LocalDate.now())
			.build();

		SimpleDiaryCreateResponse simpleDiaryCreateResponse = SimpleDiaryCreateResponse.builder()
			.id(1L)
			.content("햇살이 창문을 따뜻하게 비추고, 부드러운 바람이 커튼을 살며시 흔든다.")
			.date(LocalDate.now())
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
				requestFields(
					fieldWithPath("content").type(JsonFieldType.STRING).description("일기 내용"),
					fieldWithPath("date").type(JsonFieldType.STRING).attributes(localDateFormat()).description("일기 작성일")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
					fieldWithPath("response").type(JsonFieldType.OBJECT).description("결과 데이터"),
					fieldWithPath("response.id").type(JsonFieldType.NUMBER).description("일기 고유 번호"),
					fieldWithPath("response.content").type(JsonFieldType.STRING).description("일기 내용"),
					fieldWithPath("response.date").type(JsonFieldType.STRING).attributes(localDateFormat()).description("일기 작성일")
				)
			));

		verify(diaryService).createSimpleDiary(any(HttpServletRequest.class), any(SimpleDiaryCreateRequest.class));
	}

	@Test
	@DisplayName("일기 작성 - 자세한")
	public void createDetailedDiary() throws Exception {

		// given
		DetailedDiaryCreateRequest detailedDiaryCreateRequest = DetailedDiaryCreateRequest.builder()
			.title("오늘의 일기")
			.content("햇살이 창문을 따뜻하게 비추고, 부드러운 바람이 커튼을 살며시 흔든다. 방 안에는 은은한 커피 향이 퍼지며 아침의 고요함을 깨운다. 나는 창가에 앉아 오늘 해야 할 일들을 천천히 정리해 본다. 바쁜 하루가 예상되지만, 차분한 마음으로 시작하고 싶다. 문득 책장에 꽂힌 오랜 책 한 권이 눈에 들어온다. 몇 년 전 감명 깊게 읽었던 소설인데, 다시 펼쳐보니 첫 문장만으로도 그때의 감정이 떠오른다. 시간은 흐르지만 어떤 기억은 변하지 않는다.")
			.date(LocalDate.now())
			.build();

		DetailedDiaryCreateResponse detailedDiaryCreateResponse = DetailedDiaryCreateResponse.builder()
			.id(1L)
			.title("오늘의 일기")
			.content("햇살이 창문을 따뜻하게 비추고, 부드러운 바람이 커튼을 살며시 흔든다. 방 안에는 은은한 커피 향이 퍼지며 아침의 고요함을 깨운다. 나는 창가에 앉아 오늘 해야 할 일들을 천천히 정리해 본다. 바쁜 하루가 예상되지만, 차분한 마음으로 시작하고 싶다. 문득 책장에 꽂힌 오랜 책 한 권이 눈에 들어온다. 몇 년 전 감명 깊게 읽었던 소설인데, 다시 펼쳐보니 첫 문장만으로도 그때의 감정이 떠오른다. 시간은 흐르지만 어떤 기억은 변하지 않는다.")
			.date(LocalDate.now())
			.build();

		given(diaryService.createDetailedDiary(any(HttpServletRequest.class), any(DetailedDiaryCreateRequest.class))).willReturn(detailedDiaryCreateResponse);

		// when
		ResultActions resultActions = mockMvc.perform(
			RestDocumentationRequestBuilders.post("/diary/detailed")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(detailedDiaryCreateRequest))
				.header(HttpHeaders.AUTHORIZATION, "감사일기.Access.Token")
				.with(user("user").roles("USER"))
		);

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("code").value(200))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andDo(document("diary/detailed/create",
				getDocumentRequest(),
				getDocumentResponse(),
				requestFields(
					fieldWithPath("title").type(JsonFieldType.STRING).optional().description("일기 제목"),
					fieldWithPath("content").type(JsonFieldType.STRING).description("일기 내용"),
					fieldWithPath("date").type(JsonFieldType.STRING).attributes(localDateFormat()).description("일기 작성일")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
					fieldWithPath("response").type(JsonFieldType.OBJECT).description("결과 데이터"),
					fieldWithPath("response.id").type(JsonFieldType.NUMBER).description("일기 고유 번호"),
					fieldWithPath("response.title").type(JsonFieldType.STRING).optional().description("일기 제목"),
					fieldWithPath("response.content").type(JsonFieldType.STRING).description("일기 내용"),
					fieldWithPath("response.date").type(JsonFieldType.STRING).attributes(localDateFormat()).description("일기 작성일")
				)
			));

		verify(diaryService).createDetailedDiary(any(HttpServletRequest.class), any(DetailedDiaryCreateRequest.class));
	}

}
