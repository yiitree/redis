package com.example.redis.controller;

import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/3")
public class MyLock {

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @Resource
    private RedissonClient redissonClient1;

    // ################### 自定义锁 #####################

    // 出现问题：异常
    @GetMapping("/1")
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
    @GetMapping("/2")
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

    @GetMapping("/3")
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

    @GetMapping("/4")
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

    @GetMapping("/5")
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

    // ################### redisson #####################

    /**
     * 使用redisson
     */
    @GetMapping("/6")
    public void get5(){
        // 设置redisson锁
        RLock redissonLock = redissonClient1.getLock("product_101");
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
    @GetMapping("/7")
    public void get6(){
        // 设置redisson锁
        RReadWriteLock redissonLock = redissonClient1.getReadWriteLock("product_101");
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
