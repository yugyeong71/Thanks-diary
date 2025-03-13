package com.example.thanksdiary.dto.diary.response;

import java.util.List;

import com.example.thanksdiary.dto.diary.common.DateDetailedDiaryDto;
import com.example.thanksdiary.dto.diary.common.DateSimpleDiaryDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class DateDiaryResponse {

	private List<DateDetailedDiaryDto> detailedDiaryList;

	private List<DateSimpleDiaryDto> simpleDiaryList;

}
