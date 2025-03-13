package com.example.thanksdiary.service.diary;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.thanksdiary.dao.diary.DiaryRepository;
import com.example.thanksdiary.domain.diary.Diary;
import com.example.thanksdiary.domain.diary.enums.DiaryType;
import com.example.thanksdiary.domain.user.User;
import com.example.thanksdiary.dto.diary.common.DateDetailedDiaryDto;
import com.example.thanksdiary.dto.diary.common.DateSimpleDiaryDto;
import com.example.thanksdiary.dto.diary.request.DetailedDiaryCreateRequest;
import com.example.thanksdiary.dto.diary.request.SimpleDiaryCreateRequest;
import com.example.thanksdiary.dto.diary.response.DateDiaryResponse;
import com.example.thanksdiary.dto.diary.response.DetailedDiaryCreateResponse;
import com.example.thanksdiary.dto.diary.response.SimpleDiaryCreateResponse;
import com.example.thanksdiary.service.user.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiaryService {

	private final DiaryRepository diaryRepository;

	private final UserService userService;

	/**
	 * 간단한 일기 작성
	 */
	@Transactional
	public SimpleDiaryCreateResponse createSimpleDiary(HttpServletRequest httpServletRequest, SimpleDiaryCreateRequest simpleDiaryCreateRequest) {
		User user = userService.getUser(httpServletRequest);

		Diary diary = diaryRepository.save(Diary.builder()
				.userId(user.getId())
				.content(simpleDiaryCreateRequest.getContent())
				.recordDate(simpleDiaryCreateRequest.getDate())
				.type(DiaryType.SIMPLE)
			.build());

		return SimpleDiaryCreateResponse.builder()
			.id(diary.getId())
			.content(diary.getContent())
			.date(diary.getRecordDate())
			.build();
	}

	/**
	 * 자세한 일기 작성
	 */
	@Transactional
	public DetailedDiaryCreateResponse createDetailedDiary(HttpServletRequest httpServletRequest, DetailedDiaryCreateRequest detailedDiaryCreateRequest) {
		User user = userService.getUser(httpServletRequest);

		Diary diary = diaryRepository.save(Diary.builder()
				.userId(user.getId())
				.title(detailedDiaryCreateRequest.getTitle())
				.content(detailedDiaryCreateRequest.getContent())
				.recordDate(detailedDiaryCreateRequest.getDate())
				.type(DiaryType.DETAILED)
			.build());

		return DetailedDiaryCreateResponse.builder()
			.id(diary.getId())
			.title(diary.getTitle().equals("") ? null : diary.getTitle()) // TODO : 빈 문자열 처리 방법 수정 필요
			.content(diary.getContent())
			.date(diary.getRecordDate())
			.build();
	}

	/**
	 * 날짜별 일기 조회
	 */
	@Transactional(readOnly = true)
	public DateDiaryResponse getDiaryByDate(HttpServletRequest httpServletRequest, LocalDate date) {
		User user = userService.getUser(httpServletRequest);

		List<Diary> diaryList = diaryRepository.findAllByUserIdAndRecordDate(user.getId(), date);

		Map<DiaryType, List<Diary>> diaryListByType = diaryList.stream()
			.collect(Collectors.groupingBy(Diary::getType));

		List<DateDetailedDiaryDto> detailedDiaryList = diaryListByType.getOrDefault(DiaryType.DETAILED, Collections.emptyList())
			.stream()
			.map(diary -> DateDetailedDiaryDto.builder()
				.id(diary.getId())
				.title(diary.getTitle())
				.content(diary.getContent())
				.build())
			.toList();

		List<DateSimpleDiaryDto> simpleDiaryList = diaryListByType.getOrDefault(DiaryType.SIMPLE, Collections.emptyList())
			.stream()
			.map(diary -> DateSimpleDiaryDto.builder()
				.id(diary.getId())
				.content(diary.getContent())
				.build())
			.toList();

		return DateDiaryResponse.builder()
			.detailedDiaryList(detailedDiaryList)
			.simpleDiaryList(simpleDiaryList)
			.build();
	}
}
