package com.powernode.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping("/index")
    public String index(){
        //redisTemplate.opsForValue().set("key","value");
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.opsForValue().set("shinubi","史努比");
        return "success";
    }

    @RequestMapping("/get")
    public String get(){
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        String str = redisTemplate.opsForValue().get("shinubi").toString();
        System.out.println(str);
        return str;
    }
}
