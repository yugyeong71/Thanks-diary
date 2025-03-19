package com.example.thanksdiary.dto.diary.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class DiaryStatisticResponse {

	private int allDiaryCount;

	private int consecutiveDay;

}
