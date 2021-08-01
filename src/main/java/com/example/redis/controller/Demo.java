package com.example.redis.controller;

import org.redisson.Redisson;
import org.redisson.RedissonLock;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 曾睿
 * @Date: 2021/8/1 12:52
 */
public class Demo {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private Redisson redisson;

    // 出现问题：异常
    public void get(){
        String lockKey = "lockKey";
        // 尝试拿锁
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(lockKey, "lockValue");
        // 没有拿到锁
        if(!Boolean.TRUE.equals(aBoolean)){
            System.out.println("当前系统繁忙，请重试");
        }
        // 释放锁
        redisTemplate.delete(lockKey);
    }

    /**
     * 程序挂掉还是有问题
     */
    public void get1(){
        String lockKey = "lockKey";
        try{
            // 尝试拿锁
            Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(lockKey, "lockValue");
            // 没有拿到锁
            if(!Boolean.TRUE.equals(aBoolean)){
                System.out.println("当前系统繁忙，请重试");
            }
        }finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }

    public void get2(){
        String lockKey = "lockKey";
        try{
            // 尝试拿锁
            Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(lockKey, "lockValue");
            // 使用redis设置超时时间
            redisTemplate.expire(lockKey, 10, TimeUnit.SECONDS);
            // 没有拿到锁
            if(!Boolean.TRUE.equals(aBoolean)){
                System.out.println("当前系统繁忙，请重试");
            }
        }finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }

    public void get3(){
        String lockKey = "lockKey";
        try{
            // 尝试拿锁并使用redis设置超时时间
            Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(lockKey, "lockValue", 10, TimeUnit.SECONDS);
            // 没有拿到锁
            if(!Boolean.TRUE.equals(aBoolean)){
                System.out.println("当前系统繁忙，请重试");
            }
        }finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }

    public void get4(){
        String lockKey = "lockKey";
        try{
            // 尝试拿锁并使用redis设置超时时间
            Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(lockKey, "lockValue", 10, TimeUnit.SECONDS);
            // 没有拿到锁
            if(!Boolean.TRUE.equals(aBoolean)){
                System.out.println("当前系统繁忙，请重试");
            }
        }finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * 使用redisson
     */
    public void get5(){
        // 设置redisson锁
        RLock redissonLock = redisson.getLock("product_101");
        try{
            // 加redisson锁 --- 相当于setIfAbsent(lockKey, clientId, 10, TimeUnit.SECONDS);
            redissonLock.lock();
            // 业务内容
        }finally {
            // 释放锁
            redissonLock.unlock();
        }
    }

    /**
     * 使用redisson的读写锁
     */
    public void get6(){
        // 设置redisson锁
        RReadWriteLock redissonLock = redisson.getReadWriteLock("product_101");
        try{
            // 加redisson锁 --- 相当于setIfAbsent(lockKey, clientId, 10, TimeUnit.SECONDS);
            redissonLock.readLock();
            // 业务内容
        }finally {
            // 释放锁
            redissonLock.readLock();
        }
    }

}
