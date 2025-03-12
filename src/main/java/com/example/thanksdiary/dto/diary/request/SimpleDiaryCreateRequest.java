package com.example.thanksdiary.dto.diary.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class SimpleDiaryCreateRequest {

	@NotBlank(message = "일기 내용은 필수입니다.")
	@Size(max = 50, message = "일기는 최대 50자까지 작성할 수 있습니다.")
	private String content;

	@NotNull(message = "일기 작성일은 필수입니다.")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private LocalDate date;

}
