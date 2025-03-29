package com.example.thanksdiary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ThanksDiaryApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThanksDiaryApplication.class, args);
	}

}
