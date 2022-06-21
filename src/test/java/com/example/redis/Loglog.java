package com.example.redis;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

@SpringBootTest
public class Loglog {

    @Resource
    public RedisTemplate<String,Object> redisTemplate;

    public static final String SCORE_RANK = "position_rank";

    @Test
    public void add(){
        String ip = "111";
        String positionId = "op11";
        Long add = redisTemplate.opsForHyperLogLog().add(positionId,ip);
        System.out.println(add);
    }

    @Test
    public void get(){
        String positionId = "op11";
        String positionId1 = "op111";
        Long add = redisTemplate.opsForHyperLogLog().size(positionId);
        System.out.println(add);
    }

}
