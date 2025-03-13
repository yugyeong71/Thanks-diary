package com.example.thanksdiary.common;

import static org.springframework.restdocs.snippet.Attributes.*;

import org.springframework.restdocs.snippet.Attributes;

public class DocumentOptionalGenerator {

	public static Attributes.Attribute setFormat(String value) {
		return key("format").value(value);
	}

	public static Attributes.Attribute localDateTimeFormat() {
		return setFormat("yyyy-MM-dd HH:mm:ss");
	}

	public static Attributes.Attribute localDateFormat() {
		return setFormat("yyyy-MM-dd");
	}

	public static Attributes.Attribute dayOfWeekFormat() {
		return setFormat("SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY");
	}
}
