package com.example.thanksdiary.dao.diary;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.thanksdiary.domain.diary.QDiary;
import com.example.thanksdiary.dto.common.PagingRequest;
import com.example.thanksdiary.dto.diary.common.AllDiaryDto;
import com.example.thanksdiary.dto.diary.common.DateDiaryDto;
import com.example.thanksdiary.dto.diary.request.AllDiaryRequest;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DiaryCustomRepositoryImpl implements DiaryCustomRepository {

	private final JPAQueryFactory queryFactory;

	private final QDiary diary = QDiary.diary;

	/**
	 * 날짜별 일기 전체 조회
	 */
	@Override
	public Page<AllDiaryDto> findAllDiaryByDate(AllDiaryRequest request, Long userId) {
		Pageable paging = new PagingRequest(request.getPage(), request.getSize()).of();

		List<LocalDate> diaryDateList = queryFactory
			.select(diary.recordDate)
			.from(diary)
			.where(diary.userId.eq(userId))
			.groupBy(diary.recordDate)
			.orderBy(diary.recordDate.desc())
			.offset(paging.getOffset())
			.limit(paging.getPageSize())
			.fetch();

		List<AllDiaryDto> allDiaryList = diaryDateList.stream()
			.map(date -> new AllDiaryDto(
				date,
				date.getDayOfWeek().toString(),
				findDiaryListByDate(date, userId)
			))
			.collect(Collectors.toList());

		Long total = Optional.ofNullable(queryFactory
			.select(diary.createdAt.countDistinct())
			.from(diary)
			.where(diary.userId.eq(userId))
			.fetchOne()).orElse(0L);

		return new PageImpl<>(allDiaryList, paging, total);
	}

	private List<DateDiaryDto> findDiaryListByDate(LocalDate date, Long userId) {
		return queryFactory
			.select(Projections.constructor(DateDiaryDto.class,
				diary.id, diary.type, diary.title, diary.content
			))
			.from(diary)
			.where(diary.userId.eq(userId).and(diary.recordDate.eq(date)))
			.orderBy(diary.recordDate.desc())
			.fetch();
	}

	/**
	 * 일기 날짜 리스트 추출
	 */
	@Override
	public List<LocalDate> findDiaryDateListByUserId(Long userId) {
		return queryFactory
			.select(diary.recordDate)
			.distinct()
			.from(diary)
			.where(diary.userId.eq(userId))
			.orderBy(diary.recordDate.asc())
			.fetch();
	}

}
