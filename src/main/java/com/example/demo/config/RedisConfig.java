package com.example.demo.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;


//@Configuration
//@EnableCaching
public class RedisConfig {

//    @Bean
//    public RedisTemplate<String,Object> cacheRedisManager(RedisConnectionFactory connectionFactory){
//        RedisTemplate<String,Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
//
//        return template;
//    }
}
