package com.example.thanksdiary.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter @Setter @SuperBuilder
@AllArgsConstructor @NoArgsConstructor
public class TokenDto {

	private String accessToken;

	private String refreshToken;

}
