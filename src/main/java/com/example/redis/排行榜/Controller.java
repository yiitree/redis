package com.example.redis.排行榜;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

public class Controller {

    public static void main(String[] args) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        ZSetOperations operation = redisTemplate.opsForZSet();
        //添加日榜数据  incrementScore方法当对应的key不存在时，会自动创建对应key的集合，已存在则会增加对应value的score
        operation.incrementScore("2022-06-13:dayRank","lyx",30);
        operation.incrementScore("2022-06-13:dayRank","lisi",5);
        operation.incrementScore("2022-06-13:dayRank","zhangsan",10);
    }

    /**
     * 记录日志
     */
    public void saveLog(String positionId){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        ZSetOperations operation = redisTemplate.opsForZSet();
        //添加日榜数据  incrementScore方法当对应的key不存在时，会自动创建对应key的集合，已存在则会增加对应value的score
        operation.incrementScore("2022-06-13:dayRank","lyx",1);
        operation.incrementScore("2022-06-13:dayRank","lisi",5);
        operation.incrementScore("2022-06-13:dayRank","zhangsan",10);
    }

}
