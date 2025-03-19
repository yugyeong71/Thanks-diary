package com.example.thanksdiary.domain.common;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;

import com.example.thanksdiary.domain.common.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter @Builder @DynamicInsert
@AllArgsConstructor @NoArgsConstructor
@Table(name = "accessLog")
public class AccessLog {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "enum")
	private UserRole role;

	private String requestName;

	private String requestUrl;

	@Column(columnDefinition = "json")
	private String header;

	@Column(columnDefinition = "json")
	private String requestBody;

	@Column(columnDefinition = "json")
	private String responseBody;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private LocalDateTime createdAt;

}
