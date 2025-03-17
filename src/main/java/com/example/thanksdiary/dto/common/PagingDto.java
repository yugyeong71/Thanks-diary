package com.example.thanksdiary.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class PagingDto {

	private int number;

	private int totalPages;

	private long totalElements;

	public int getNumber() {
		return number + 1;
	}

}
