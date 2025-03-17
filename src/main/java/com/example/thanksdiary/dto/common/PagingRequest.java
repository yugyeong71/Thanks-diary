package com.example.thanksdiary.dto.common;

import org.springframework.data.domain.PageRequest;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PagingRequest {

	private int page;

	private int size;

	public PageRequest of() {
		return PageRequest.of(page - 1, size);
	}

}

