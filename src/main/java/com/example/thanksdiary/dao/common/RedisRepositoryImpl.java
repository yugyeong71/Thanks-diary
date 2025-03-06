package com.example.thanksdiary.dao.common;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisRepositoryImpl implements RedisRepository {

	private final RedisTemplate<String, String> redisTemplate;

	@Override
	public String getData(String key) {
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		return valueOperations.get(key);
	}

	@Override
	public boolean existData(String key) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}

	@Override
	public void setDataExpire(String key, String value, long timeout) {
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		Duration duration = Duration.ofMillis(timeout);
		valueOperations.set(key, value, duration);
	}

	@Override
	public void deleteData(String key) {
		redisTemplate.delete(key);
	}
}
