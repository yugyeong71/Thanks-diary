package com.example.thanksdiary.domain.diary.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DiaryType {

	SIMPLE("간단한 일기"),

	DETAILED("자세한 일기");

	private final String description;

}
