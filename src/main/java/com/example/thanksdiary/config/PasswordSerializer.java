package com.example.thanksdiary.config;

import java.io.IOException;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PasswordSerializer extends JsonSerializer<String> {

	private final PasswordEncoder passwordEncoder;

	@Override
	public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws
		IOException {
		jsonGenerator.writeString(passwordEncoder.encode(s));
	}

	@Override
	public Class<String> handledType() {
		return String.class;
	}

}
