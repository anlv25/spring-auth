package com.alibou.security.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void save(String tblName,String key ,Object obj) {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.hashCommands().hSet(
                    tblName.getBytes(StandardCharsets.UTF_8),
                    key.getBytes(StandardCharsets.UTF_8),
                    serializeUser(obj)
            );
            return null;
        });
    }

    public Object getData(String tblName,String key) {
        return  redisTemplate.execute((RedisCallback<Object>) connection -> {
            byte[] value = connection.hashCommands().hGet(
                    tblName.getBytes(StandardCharsets.UTF_8),
                    key.getBytes(StandardCharsets.UTF_8)
            );
            return deserializeUser(value);
        });
    }

    private byte[] serializeUser(Object user) {
        try {
            return objectMapper.writeValueAsBytes(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    };

    private Object deserializeUser(byte[] value) {
        try {
            return objectMapper.readValue(value, Object.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
