package com.example.thanksdiary.domain.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {

	SUCCESS_MESSAGE("API Call successful", "정상"),

	ALREADY_EXIST_DATA_MESSAGE("Already Exist Data", "이미 존재하는 데이터");

	private final String message;

	private final String description;

}
