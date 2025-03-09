package com.example.thanksdiary.dto.user.response;

import com.example.thanksdiary.dto.common.TokenDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter @SuperBuilder
@AllArgsConstructor @NoArgsConstructor
public class UserLoginResponse extends TokenDto {

	private Long id;

	private String email;

}
