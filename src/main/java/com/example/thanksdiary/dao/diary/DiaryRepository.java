package com.example.thanksdiary.dao.diary;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.thanksdiary.domain.diary.Diary;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

	List<Diary> findAllByUserIdAndRecordDate(Long userId, LocalDate date);

	int countByUserId(Long userId);

}
