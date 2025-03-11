package com.example.thanksdiary.dao.diary;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.thanksdiary.domain.diary.Diary;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
}
