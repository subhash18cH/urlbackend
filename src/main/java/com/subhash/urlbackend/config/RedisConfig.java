package com.subhash.urlbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory connectionFactory(){
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<String,Object>redisTemplate(){
        RedisTemplate<String,Object>redisTemplate=new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    //for production use
//    @Bean
//    public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
//        FilterRegistrationBean<ForwardedHeaderFilter> filterBean = new FilterRegistrationBean<>();
//        filterBean.setFilter(new ForwardedHeaderFilter());
//        return filterBean;
//    }

}