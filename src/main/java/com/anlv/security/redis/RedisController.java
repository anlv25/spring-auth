package com.anlv.security.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
@AllArgsConstructor
public class RedisController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisService redisService;


//    @GetMapping("/list/{key}")
//    public void saveList(@PathVariable String key) {
//        List<User> users = List.of(new User("anlv", "tesch"), new User("anlv2", "tesch2"), new User("anlv3", "tesch3"));
//        redisService.save("user", key, users);
//    }
//
//    @GetMapping("/get-list/{key}")
//    public List<User> getUserByUsername(@PathVariable String key) {
//        String result = (String) redisTemplate.opsForValue().get(key);
//
//        return (List<User>) redisService.getData("user", key);
//    }
}