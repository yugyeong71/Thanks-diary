package com.example.thanksdiary.dto.diary.common;

import com.example.thanksdiary.domain.diary.enums.DiaryType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class DateDiaryDto {

	private Long id;

	private DiaryType type;

	private String title;

	private String content;

}
