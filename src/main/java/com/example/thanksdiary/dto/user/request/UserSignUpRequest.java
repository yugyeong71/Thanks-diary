package com.example.thanksdiary.dto.user.request;

import com.example.thanksdiary.config.PasswordSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class UserSignUpRequest {

	@NotBlank(message = "이메일은 필수입니다.")
	@Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$", message = "올바르지 않은 이메일입니다.")
	private String email;

	@NotBlank(message = "비밀번호는 필수입니다.")
	@Size(min = 6, max = 20, message = "비밀번호는 최소 6자부터 최대 20자입니다.")
	@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^&*])[a-zA-Z!@#$%^&*0-9]+$", message = "비밀번호는 특수문자 포함 6자 이상 20자 이하입니다.")
	@JsonSerialize(using = PasswordSerializer.class)
	private String password;

	@NotBlank(message = "인증 코드는 필수입니다.")
	private String code;

}
