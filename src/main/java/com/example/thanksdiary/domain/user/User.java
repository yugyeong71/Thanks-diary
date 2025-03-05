package com.example.thanksdiary.domain.user;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;

import com.example.thanksdiary.domain.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
@SQLRestriction("deletedAt IS NULL")
@Table(name = "user")
public class User extends BaseEntity {

	@Column(length = 100, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = true)
	private String refreshToken;

	@Column(nullable = true)
	private LocalDateTime lastLoginDate;

}
