package com.example.thanksdiary.dto.diary.common;

import java.time.LocalDate;

import com.example.thanksdiary.domain.diary.enums.DiaryType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class DateDiaryDto {

	private Long id;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate date;

	private String dayOfWeek;

	private DiaryType type;

	private String title;

	private String content;

}
