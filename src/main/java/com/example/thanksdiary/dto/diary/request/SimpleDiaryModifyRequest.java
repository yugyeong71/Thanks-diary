package com.example.thanksdiary.dto.diary.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class SimpleDiaryModifyRequest {

	@NotNull(message = "일기 고유 번호는 필수입니다.")
	private Long id;

	@NotBlank(message = "일기 내용은 필수입니다.")
	@Size(max = 50, message = "일기는 최대 50자까지 작성할 수 있습니다.")
	private String content;

}
