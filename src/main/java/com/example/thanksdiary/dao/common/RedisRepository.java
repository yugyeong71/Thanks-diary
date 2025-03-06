package com.example.thanksdiary.dao.common;

public interface RedisRepository {

	String getData(String key);

	boolean existData(String key);

	void setDataExpire(String key, String value, long timeout);

	void deleteData(String key);

}
