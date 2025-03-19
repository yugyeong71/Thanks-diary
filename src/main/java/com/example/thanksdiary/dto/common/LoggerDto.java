package com.example.thanksdiary.dto.common;

import com.example.thanksdiary.domain.common.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class LoggerDto {

	private Long userId;

	private UserRole role;

	private String requestName;

	private String requestUrl;

	private String header;

	private String requestBody;

}
