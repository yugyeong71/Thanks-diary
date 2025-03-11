package com.example.thanksdiary.service.diary;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.thanksdiary.dao.diary.DiaryRepository;
import com.example.thanksdiary.domain.diary.Diary;
import com.example.thanksdiary.domain.diary.enums.DiaryType;
import com.example.thanksdiary.domain.user.User;
import com.example.thanksdiary.dto.diary.request.SimpleDiaryCreateRequest;
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
				.type(DiaryType.SIMPLE)
			.build());

		return SimpleDiaryCreateResponse.builder()
			.id(diary.getId())
			.content(diary.getContent())
			.createdAt(diary.getCreatedAt())
			.build();
	}
}
