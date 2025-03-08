package com.example.thanksdiary.domain.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {

	ROLE_USER(1, "회원 권한", "사용자");

	private final int id;

	private final String description;

	private final String value;

}
