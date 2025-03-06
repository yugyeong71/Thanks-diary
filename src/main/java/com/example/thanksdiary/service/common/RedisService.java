package com.example.thanksdiary.service.common;

import org.springframework.stereotype.Service;

import com.example.thanksdiary.dao.common.RedisRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {

	private final RedisRepository redisRepository;

	public String getData(String key) {
		return redisRepository.getData(key);
	}

	public boolean existData(String key) {
		return redisRepository.existData(key);
	}

	public void setDataExpire(String key, String value, long timeout) {
		redisRepository.setDataExpire(key, value, timeout);
	}

	public void deleteData(String key) {
		redisRepository.deleteData(key);
	}
}

