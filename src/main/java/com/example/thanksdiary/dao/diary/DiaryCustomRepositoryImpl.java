package com.example.thanksdiary.dao.diary;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.thanksdiary.domain.diary.QDiary;
import com.example.thanksdiary.dto.common.PagingRequest;
import com.example.thanksdiary.dto.diary.common.DateDiaryDto;
import com.example.thanksdiary.dto.diary.request.AllDiaryRequest;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
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
	public Page<DateDiaryDto> findAllDiaryByDate(AllDiaryRequest request, Long userId) {
		Pageable paging = new PagingRequest(request.getPage(), request.getSize()).of();

		List<DateDiaryDto> diaryList = queryFactory
			.select(Projections.constructor(DateDiaryDto.class,
				diary.id, diary.recordDate, Expressions.stringTemplate("DAYNAME({0})", diary.recordDate), diary.type, diary.title, diary.content
			))
			.from(diary)
			.where(diary.userId.eq(userId))
			.orderBy(diary.recordDate.desc(), diary.createdAt.desc())
			.offset(paging.getOffset())
			.limit(paging.getPageSize())
			.fetch();

		Long total = Optional.ofNullable(queryFactory
			.select(diary.count())
			.from(diary)
			.where(diary.userId.eq(userId))
			.fetchOne()).orElse(0L);

		return new PageImpl<>(diaryList, paging, total);
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
