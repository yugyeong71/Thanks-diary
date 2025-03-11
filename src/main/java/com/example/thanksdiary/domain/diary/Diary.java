package com.example.thanksdiary.domain.diary;

import org.hibernate.annotations.SQLRestriction;

import com.example.thanksdiary.domain.common.BaseEntity;
import com.example.thanksdiary.domain.diary.enums.DiaryType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
@SQLRestriction("deletedAt IS NULL")
@Table(name = "diary")
public class Diary extends BaseEntity {

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = true)
	private String title;

	@Lob
	@Column(columnDefinition = "TEXT", nullable = false)
	private String content;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DiaryType type;

}
