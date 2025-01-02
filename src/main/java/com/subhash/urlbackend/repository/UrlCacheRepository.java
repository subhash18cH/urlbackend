package com.subhash.urlbackend.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UrlCacheRepository {

    @Autowired
    private RedisTemplate<String,Object>redisTemplate;

    private static final String URL_KEY_PREFIX = "short_url:";
    private static final String REVERSE_URL_KEY_PREFIX = "long_url:";

    public void saveToCache(String shortUrl, String longUrl) {
        redisTemplate.opsForValue().set(URL_KEY_PREFIX + shortUrl, longUrl);
        redisTemplate.opsForValue().set(REVERSE_URL_KEY_PREFIX + longUrl, shortUrl);
    }

    public String getLongUrlFromCache(String shortUrl) {
        Object value = redisTemplate.opsForValue().get(URL_KEY_PREFIX + shortUrl);
        return value != null ? value.toString() : null;
    }

    public String getShortUrlFromCache(String longUrl) {
        Object value = redisTemplate.opsForValue().get(REVERSE_URL_KEY_PREFIX + longUrl);
        return value != null ? value.toString() : null;
    }

    public void removeUrlFromCache(String shortUrl, String longUrl) {
        redisTemplate.delete(URL_KEY_PREFIX + shortUrl);
        redisTemplate.delete(REVERSE_URL_KEY_PREFIX + longUrl);
    }
}
