package com.example.thanksdiary.service.diary;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.thanksdiary.common.exception.BadRequestException;
import com.example.thanksdiary.common.exception.ForbiddenException;
import com.example.thanksdiary.common.exception.NotFoundDataException;
import com.example.thanksdiary.dao.diary.DiaryCustomRepository;
import com.example.thanksdiary.dao.diary.DiaryRepository;
import com.example.thanksdiary.domain.diary.Diary;
import com.example.thanksdiary.domain.diary.enums.DiaryType;
import com.example.thanksdiary.domain.user.User;
import com.example.thanksdiary.dto.common.PagingDto;
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
import com.example.thanksdiary.dto.diary.response.DiaryStatisticResponse;
import com.example.thanksdiary.dto.diary.response.SimpleDiaryCreateResponse;
import com.example.thanksdiary.dto.diary.response.SimpleDiaryModifyResponse;
import com.example.thanksdiary.service.user.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiaryService {

	private final DiaryRepository diaryRepository;

	private final UserService userService;

	private final DiaryCustomRepository diaryCustomRepository;

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

	/**
	 * 자세한 일기 상세 조회
	 */
	@Transactional(readOnly = true)
	public DetailedDiaryResponse getDetailedDiary(HttpServletRequest httpServletRequest, Long id) {
		User user = userService.getUser(httpServletRequest);

		Diary diary = diaryRepository.findById(id).orElseThrow(() -> new NotFoundDataException("존재하지 않는 일기입니다."));

		if (!user.getId().equals(diary.getUserId())) {
			throw new ForbiddenException("해당 일기에 대한 접근 권한이 없습니다.");
		}

		if (diary.getType() != DiaryType.DETAILED) {
			throw new BadRequestException("자세한 일기만 조회 가능합니다.");
		}

		return DetailedDiaryResponse.builder()
			.date(diary.getRecordDate())
			.dayOfWeek(diary.getRecordDate().getDayOfWeek().toString())
			.title(diary.getTitle())
			.content(diary.getContent())
			.build();
	}

	/**
	 * 일기 전체 조회
	 */
	@Transactional(readOnly = true)
	public AllDiaryResponse getAllDiary(HttpServletRequest httpServletRequest, AllDiaryRequest allDiaryRequest) {
		User user = userService.getUser(httpServletRequest);

		Page<DateDiaryDto> allDiaryDto = diaryCustomRepository.findAllDiaryByDate(allDiaryRequest, user.getId());

		PagingDto pagingDto = new PagingDto(allDiaryDto.getNumber(), allDiaryDto.getTotalPages(), allDiaryDto.getTotalElements());

		return AllDiaryResponse.builder()
			.paging(pagingDto)
			.allDiaryList(allDiaryDto.getContent())
			.build();
	}

	/**
	 * 간단한 일기 수정
	 */
	@Transactional
	public SimpleDiaryModifyResponse modifySimpleDiary(HttpServletRequest httpServletRequest, SimpleDiaryModifyRequest simpleDiaryModifyRequest) {
		User user = userService.getUser(httpServletRequest);

		Diary diary = diaryRepository.findById(simpleDiaryModifyRequest.getId()).orElseThrow(() -> new NotFoundDataException("존재하지 않는 일기입니다."));

		if (!user.getId().equals(diary.getUserId())) {
			throw new ForbiddenException("해당 일기에 대한 접근 권한이 없습니다.");
		}

		diary.modifySimpleDiary(simpleDiaryModifyRequest.getContent());

		return SimpleDiaryModifyResponse.builder()
			.content(diary.getContent())
			.date(diary.getRecordDate())
			.build();
	}

	/**
	 * 자세한 일기 수정
	 */
	@Transactional
	public DetailedDiaryModifyResponse modifyDetailedDiary(HttpServletRequest httpServletRequest, DetailedDiaryModifyRequest detailedDiaryModifyRequest) {
		User user = userService.getUser(httpServletRequest);

		Diary diary = diaryRepository.findById(detailedDiaryModifyRequest.getId()).orElseThrow(() -> new NotFoundDataException("존재하지 않는 일기입니다."));

		if (!user.getId().equals(diary.getUserId())) {
			throw new ForbiddenException("해당 일기에 대한 접근 권한이 없습니다.");
		}

		diary.modifyDetailedDiary(detailedDiaryModifyRequest.getTitle(), detailedDiaryModifyRequest.getContent());

		return DetailedDiaryModifyResponse.builder()
			.title(diary.getTitle().equals("") ? null : diary.getTitle())
			.content(diary.getContent())
			.date(diary.getRecordDate())
			.build();
	}

	/**
	 * 일기 삭제
	 */
	@Transactional
	public void deleteDiary(HttpServletRequest httpServletRequest, Long id) {
		User user = userService.getUser(httpServletRequest);

		Diary diary = diaryRepository.findById(id).orElseThrow(() -> new NotFoundDataException("존재하지 않는 일기입니다."));

		if (!user.getId().equals(diary.getUserId())) {
			throw new ForbiddenException("해당 일기에 대한 삭제 권한이 없습니다.");
		}

		diary.deleteDiary();
	}

	/**
	 * 일기 통계 조회
	 */
	@Transactional(readOnly = true)
	public DiaryStatisticResponse statisticsDiary(HttpServletRequest httpServletRequest) {
		User user = userService.getUser(httpServletRequest);

		int allDiaryCount = diaryRepository.countByUserId(user.getId());

		int consecutiveDay = findMaxConsecutiveDay(user.getId());

		return DiaryStatisticResponse.builder()
			.allDiaryCount(allDiaryCount)
			.consecutiveDay(consecutiveDay)
			.build();
	}

	/**
	 * 최대 연속 기록일 추출
	 */
	public int findMaxConsecutiveDay(Long userId) {
		List<LocalDate> diaryDateList = diaryCustomRepository.findDiaryDateListByUserId(userId);

		if (diaryDateList.isEmpty()) {
			return 0;
		}

		int maxStreak = 1;
		int currentStreak = 1;

		for (int i = 1; i < diaryDateList.size(); i++) {
			LocalDate prevDate = diaryDateList.get(i - 1);
			LocalDate currentDate = diaryDateList.get(i);

			if (currentDate.equals(prevDate.plusDays(1))) {
				currentStreak++;
			} else {
				maxStreak = Math.max(maxStreak, currentStreak);
				currentStreak = 1;
			}
		}

		return Math.max(maxStreak, currentStreak);
	}

}
