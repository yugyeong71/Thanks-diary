package com.example.thanksdiary.dto.user.request;

import com.example.thanksdiary.config.PasswordSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class UserLoginRequest {

	@NotBlank(message = "이메일은 필수입니다.")
	@Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$", message = "올바르지 않은 이메일입니다.")
	private String email;

	@NotBlank(message = "비밀번호는 필수입니다.")
	@JsonSerialize(using = PasswordSerializer.class)
	private String password;

}
