package com.example.thanksdiary.domain.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenType {

	ACCESS_TOKEN(1, "Access Token", 3 * 60 * 60 * 1000L), // 3시간

	REFRESH_TOKEN(2, "Refresh Token", 14 * 24 * 60 * 60 * 1000L); // 14일

	private final int id;

	private final String description;

	private final long expirationTime;

}
