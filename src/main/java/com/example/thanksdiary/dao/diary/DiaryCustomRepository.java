package com.example.thanksdiary.dao.diary;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;

import com.example.thanksdiary.dto.diary.common.DateDiaryDto;
import com.example.thanksdiary.dto.diary.request.AllDiaryRequest;

public interface DiaryCustomRepository {

	Page<DateDiaryDto> findAllDiaryByDate(AllDiaryRequest request, Long userId);

	List<LocalDate> findDiaryDateListByUserId(Long userId);

}
