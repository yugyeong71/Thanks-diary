package com.example.thanksdiary.dto.diary.response;

import java.util.List;

import com.example.thanksdiary.dto.common.PagingDto;
import com.example.thanksdiary.dto.diary.common.DateDiaryDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class AllDiaryResponse {

	private PagingDto paging;

	private List<DateDiaryDto> allDiaryList;

}
