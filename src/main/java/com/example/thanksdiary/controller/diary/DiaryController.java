package com.example.thanksdiary.controller.diary;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.thanksdiary.dto.common.DataResponse;
import com.example.thanksdiary.dto.diary.request.DetailedDiaryCreateRequest;
import com.example.thanksdiary.dto.diary.request.SimpleDiaryCreateRequest;
import com.example.thanksdiary.dto.diary.response.DateDiaryResponse;
import com.example.thanksdiary.dto.diary.response.DetailedDiaryCreateResponse;
import com.example.thanksdiary.dto.diary.response.SimpleDiaryCreateResponse;
import com.example.thanksdiary.service.diary.DiaryService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diary")
public class DiaryController {

	private final DiaryService diaryService;

	@PostMapping(value = "/simple", name = "간단한 일기 작성")
	public DataResponse<SimpleDiaryCreateResponse> createSimpleDiary(HttpServletRequest httpServletRequest, @RequestBody @Valid SimpleDiaryCreateRequest simpleDiaryCreateRequest) {
		return new DataResponse<>(diaryService.createSimpleDiary(httpServletRequest, simpleDiaryCreateRequest));
	}

	@PostMapping(value = "/detailed", name = "자세한 일기 작성")
	private DataResponse<DetailedDiaryCreateResponse> createDetailedDiary(HttpServletRequest httpServletRequest, @RequestBody @Valid DetailedDiaryCreateRequest detailedDiaryCreateRequest) {
		return new DataResponse<>(diaryService.createDetailedDiary(httpServletRequest, detailedDiaryCreateRequest));
	}

	@GetMapping(value = "/date", name = "날짜별 일기 조회")
	private DataResponse<DateDiaryResponse> getDiaryByDate(HttpServletRequest httpServletRequest, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		return new DataResponse<>(diaryService.getDiaryByDate(httpServletRequest, date));
	}

}
