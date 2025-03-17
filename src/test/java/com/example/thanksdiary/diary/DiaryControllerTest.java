package com.example.thanksdiary.diary;

import static com.example.thanksdiary.common.ApiDocumentUtils.*;
import static com.example.thanksdiary.common.DocumentOptionalGenerator.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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
import com.example.thanksdiary.domain.diary.enums.DiaryType;
import com.example.thanksdiary.dto.common.PagingDto;
import com.example.thanksdiary.dto.diary.common.AllDiaryDto;
import com.example.thanksdiary.dto.diary.common.DateDetailedDiaryDto;
import com.example.thanksdiary.dto.diary.common.DateDiaryDto;
import com.example.thanksdiary.dto.diary.common.DateSimpleDiaryDto;
import com.example.thanksdiary.dto.diary.request.AllDiaryRequest;
import com.example.thanksdiary.dto.diary.request.DetailedDiaryCreateRequest;
import com.example.thanksdiary.dto.diary.request.DetailedDiaryModifyRequest;
import com.example.thanksdiary.dto.diary.request.SimpleDiaryCreateRequest;
import com.example.thanksdiary.dto.diary.request.SimpleDiaryModifyRequest;
import com.example.thanksdiary.dto.diary.response.AllDiaryResponse;
import com.example.thanksdiary.dto.diary.response.DateDiaryResponse;
import com.example.thanksdiary.dto.diary.response.DetailedDiaryCreateResponse;
import com.example.thanksdiary.dto.diary.response.DetailedDiaryModifyResponse;
import com.example.thanksdiary.dto.diary.response.DetailedDiaryResponse;
import com.example.thanksdiary.dto.diary.response.SimpleDiaryCreateResponse;
import com.example.thanksdiary.dto.diary.response.SimpleDiaryModifyResponse;
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

	@Test
	@DisplayName("날짜별 일기 조회")
	public void getDiaryByDate() throws Exception {

		// given
		List<DateDetailedDiaryDto> detailedDiaryList = Arrays.asList(
			DateDetailedDiaryDto.builder()
				.id(3L)
				.title(null)
				.content("오늘은 놀이공원에 갔다. 사람이 별로 없어서 놀이기구를 7개나 탔다. 감사하다!")
				.build(),
			DateDetailedDiaryDto.builder()
				.id(5L)
				.title("나는 날씨요정")
				.content("오늘은 제주 여행 첫째날이다. 비 소식이 있어서 우산을 챙기려 했는데 숙소에서 나오자마자 비가 그쳤다! 나는 날씨요정인가보다. 감사하다!")
				.build()
		);

		List<DateSimpleDiaryDto> simpleDiaryList = Arrays.asList(
			DateSimpleDiaryDto.builder()
				.id(4L)
				.content("오늘은 제주 엄청난 맛집에 갔다. 기본 웨이팅 1시간인데, 이게 뭐람! 딱 한자리가 남아있어서 바로 들어갔다. 럭키비키쟈냐...")
				.build()
		);

		DateDiaryResponse dateDiaryResponse = DateDiaryResponse.builder()
			.detailedDiaryList(detailedDiaryList)
			.simpleDiaryList(simpleDiaryList)
			.build();

		given(diaryService.getDiaryByDate(any(HttpServletRequest.class), any(LocalDate.class))).willReturn(dateDiaryResponse);

		// when
		ResultActions resultActions = mockMvc.perform(
			RestDocumentationRequestBuilders.get("/diary/date?date={date}", LocalDate.now())
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "감사일기.Access.Token")
				.with(user("user").roles("USER"))
		);

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("code").value(200))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andDo(document("diary/date",
				getDocumentRequest(),
				getDocumentResponse(),
				queryParameters(
					parameterWithName("date").attributes(localDateFormat()).description("일기 작성일 (조회 날짜)")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
					fieldWithPath("response").type(JsonFieldType.OBJECT).description("결과 데이터"),
					fieldWithPath("response.detailedDiaryList[]").type(JsonFieldType.ARRAY).description("사용자 목록"),
					fieldWithPath("response.detailedDiaryList[].id").type(JsonFieldType.NUMBER).description("일기 고유 번호"),
					fieldWithPath("response.detailedDiaryList[].title").type(JsonFieldType.STRING).optional().description("일기 제목"),
					fieldWithPath("response.detailedDiaryList[].content").type(JsonFieldType.STRING).description("일기 내용"),
					fieldWithPath("response.simpleDiaryList[]").type(JsonFieldType.ARRAY).description("사용자 목록"),
					fieldWithPath("response.simpleDiaryList[].id").type(JsonFieldType.NUMBER).description("일기 고유 번호"),
					fieldWithPath("response.simpleDiaryList[].content").type(JsonFieldType.STRING).description("일기 내용")
				)
			));

		verify(diaryService).getDiaryByDate(any(HttpServletRequest.class), any(LocalDate.class));
	}

	@Test
	@DisplayName("자세한 일기 상세 조회")
	public void getDetailedDiary() throws Exception {

		// given
		DetailedDiaryResponse detailedDiaryResponse = DetailedDiaryResponse.builder()
			.date(LocalDate.now())
			.dayOfWeek(LocalDate.now().getDayOfWeek().toString())
			.title("나홀로 영화관에")
			.content("오늘은 인생 처음으로 혼영을 했다! 혼자 영화관에 가면 심심할 것 같았는데 은근 재밌었다. 영화관에 사람도 없어서 대관한 느낌이었다. 오늘도 감사한 하루!")
			.build();

		given(diaryService.getDetailedDiary(any(HttpServletRequest.class), any(Long.class))).willReturn(detailedDiaryResponse);

		// when
		ResultActions resultActions = mockMvc.perform(
			RestDocumentationRequestBuilders.get("/diary/detailed?id={id}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "감사일기.Access.Token")
				.with(user("user").roles("USER"))
		);

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("code").value(200))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andDo(document("diary/detailed/get",
				getDocumentRequest(),
				getDocumentResponse(),
				queryParameters(
					parameterWithName("id").description("일기 고유 번호")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
					fieldWithPath("response").type(JsonFieldType.OBJECT).description("결과 데이터"),
					fieldWithPath("response.date").type(JsonFieldType.STRING).attributes(localDateFormat()).description("일기 작성일"),
					fieldWithPath("response.dayOfWeek").type(JsonFieldType.STRING).attributes(dayOfWeekFormat()).description("일기 작성 요일"),
					fieldWithPath("response.title").type(JsonFieldType.STRING).optional().description("일기 제목"),
					fieldWithPath("response.content").type(JsonFieldType.STRING).description("일기 내용")
				)
			));

		verify(diaryService).getDetailedDiary(any(HttpServletRequest.class), any(Long.class));
	}

	@Test
	@DisplayName("일기 전체 조회")
	public void getAllDiary() throws Exception {

		// given
		LocalDate today = LocalDate.now();

		List<DateDiaryDto> dateDiaryDto1 = List.of(new DateDiaryDto(10L, DiaryType.DETAILED, "오늘의 일기", "오늘은 짜장밥을 먹었다."));
		List<DateDiaryDto> dateDiaryDto2 = List.of(new DateDiaryDto(8L, DiaryType.DETAILED, null, "오늘은 수영장에 갔다."));
		List<DateDiaryDto> dateDiaryDto3 = List.of(new DateDiaryDto(7L, DiaryType.SIMPLE, null, "오늘은 호수공원에 갔다."));

		AllDiaryResponse userList = AllDiaryResponse.builder()
			.paging(new PagingDto(1, 2, 15))
			.allDiaryList(List.of(
				new AllDiaryDto(today, today.getDayOfWeek().toString(), dateDiaryDto1),
				new AllDiaryDto(today.minusDays(1), today.minusDays(1).getDayOfWeek().toString(), dateDiaryDto2),
				new AllDiaryDto(today.minusDays(2), today.minusDays(2).getDayOfWeek().toString(), dateDiaryDto3)
			))
			.build();

		given(diaryService.getAllDiary(any(HttpServletRequest.class), any(AllDiaryRequest.class))).willReturn(userList);

		// when
		ResultActions result = mockMvc.perform(
			RestDocumentationRequestBuilders.get("/diary?page={page}&size={size}", 1, 10)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "감사일기.Access.Token")
				.with(user("user").roles("USER"))
		);

		// then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("code").value(200))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andDo(document("diary/all",
				getDocumentRequest(),
				getDocumentResponse(),
				queryParameters(
					parameterWithName("page").description("요청 페이지"),
					parameterWithName("size").description("한 페이지에 조회할 데이터 개수")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
					fieldWithPath("response").type(JsonFieldType.OBJECT).description("결과 데이터"),
					fieldWithPath("response.paging").type(JsonFieldType.OBJECT).description("페이징 정보"),
					fieldWithPath("response.paging.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
					fieldWithPath("response.paging.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 개수"),
					fieldWithPath("response.paging.totalElements").type(JsonFieldType.NUMBER).description("총 데이터 개수"),
					fieldWithPath("response.allDiaryList[]").type(JsonFieldType.ARRAY).optional().description("전체 일기 목록"),
					fieldWithPath("response.allDiaryList[].date").type(JsonFieldType.STRING).description("일기 작성일"),
					fieldWithPath("response.allDiaryList[].dayOfWeek").type(JsonFieldType.STRING).attributes(dayOfWeekFormat()).description("일기 작성 요일"),
					fieldWithPath("response.allDiaryList[].diaryList[]").type(JsonFieldType.ARRAY).optional().description("날짜별 일기 목록"),
					fieldWithPath("response.allDiaryList[].diaryList[].id").type(JsonFieldType.NUMBER).description("일기 고유 번호"),
					fieldWithPath("response.allDiaryList[].diaryList[].type").type(JsonFieldType.STRING).attributes(diaryTypeFormat()).description("일기 타입"),
					fieldWithPath("response.allDiaryList[].diaryList[].title").type(JsonFieldType.STRING).optional().description("일기 제목"),
					fieldWithPath("response.allDiaryList[].diaryList[].content").type(JsonFieldType.STRING).description("일기 내용")
				)
			));

		verify(diaryService).getAllDiary(any(HttpServletRequest.class), any(AllDiaryRequest.class));
	}

	@Test
	@DisplayName("일기 수정 - 간단한")
	public void modifySimpleDiary() throws Exception {

		// given
		SimpleDiaryModifyRequest simpleDiaryModifyRequest = SimpleDiaryModifyRequest.builder()
			.id(1L)
			.content("햇살이 창문을 따뜻하게 비추고, 차가운 바람이 커튼을 살며시 흔든다.")
			.build();

		SimpleDiaryModifyResponse simpleDiaryModifyResponse = SimpleDiaryModifyResponse.builder()
			.content("햇살이 창문을 따뜻하게 비추고, 차가운 바람이 커튼을 살며시 흔든다.")
			.date(LocalDate.now())
			.build();

		given(diaryService.modifySimpleDiary(any(HttpServletRequest.class), any(SimpleDiaryModifyRequest.class))).willReturn(simpleDiaryModifyResponse);

		// when
		ResultActions resultActions = mockMvc.perform(
			RestDocumentationRequestBuilders.put("/diary/simple")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(simpleDiaryModifyRequest))
				.header(HttpHeaders.AUTHORIZATION, "감사일기.Access.Token")
				.with(user("user").roles("USER"))
		);

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("code").value(200))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andDo(document("diary/simple/modify",
				getDocumentRequest(),
				getDocumentResponse(),
				requestFields(
					fieldWithPath("id").type(JsonFieldType.NUMBER).description("일기 고유 번호"),
					fieldWithPath("content").type(JsonFieldType.STRING).description("일기 내용")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
					fieldWithPath("response").type(JsonFieldType.OBJECT).description("결과 데이터"),
					fieldWithPath("response.content").type(JsonFieldType.STRING).description("일기 내용"),
					fieldWithPath("response.date").type(JsonFieldType.STRING).attributes(localDateFormat()).description("일기 작성일")
				)
			));

		verify(diaryService).modifySimpleDiary(any(HttpServletRequest.class), any(SimpleDiaryModifyRequest.class));
	}

	@Test
	@DisplayName("일기 수정 - 자세한")
	public void modifyDetailedDiary() throws Exception {

		// given
		DetailedDiaryModifyRequest detailedDiaryModifyRequest = DetailedDiaryModifyRequest.builder()
			.id(1L)
			.title("오늘의 날씨")
			.content("햇살이 창문을 따뜻하게 비추고, 차가운 바람이 커튼을 살며시 흔든다.")
			.build();

		DetailedDiaryModifyResponse detailedDiaryModifyResponse = DetailedDiaryModifyResponse.builder()
			.title("오늘의 날씨")
			.content("햇살이 창문을 따뜻하게 비추고, 차가운 바람이 커튼을 살며시 흔든다.")
			.date(LocalDate.now())
			.build();

		given(diaryService.modifyDetailedDiary(any(HttpServletRequest.class), any(DetailedDiaryModifyRequest.class))).willReturn(detailedDiaryModifyResponse);

		// when
		ResultActions resultActions = mockMvc.perform(
			RestDocumentationRequestBuilders.put("/diary/detailed")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(detailedDiaryModifyRequest))
				.header(HttpHeaders.AUTHORIZATION, "감사일기.Access.Token")
				.with(user("user").roles("USER"))
		);

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("code").value(200))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andDo(document("diary/detailed/modify",
				getDocumentRequest(),
				getDocumentResponse(),
				requestFields(
					fieldWithPath("id").type(JsonFieldType.NUMBER).description("일기 고유 번호"),
					fieldWithPath("title").type(JsonFieldType.STRING).optional().description("일기 제목"),
					fieldWithPath("content").type(JsonFieldType.STRING).description("일기 내용")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과 코드"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
					fieldWithPath("response").type(JsonFieldType.OBJECT).description("결과 데이터"),
					fieldWithPath("response.title").type(JsonFieldType.STRING).optional().description("일기 제목"),
					fieldWithPath("response.content").type(JsonFieldType.STRING).description("일기 내용"),
					fieldWithPath("response.date").type(JsonFieldType.STRING).attributes(localDateFormat()).description("일기 작성일")
				)
			));

		verify(diaryService).modifyDetailedDiary(any(HttpServletRequest.class), any(DetailedDiaryModifyRequest.class));
	}

}
