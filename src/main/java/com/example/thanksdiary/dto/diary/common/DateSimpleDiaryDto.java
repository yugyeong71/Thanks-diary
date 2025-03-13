package com.example.thanksdiary.dto.diary.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class DateSimpleDiaryDto {

	private Long id;

	private String content;

}
