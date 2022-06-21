package com.example.redis;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient1;
    @Resource
    private RedissonClient redissonClient2;
    @Resource
    private RedissonClient redissonClient3;

    @GetMapping("/get11")
    public String get11() throws InterruptedException {
        // 创建锁对象（可重复锁）
        RLock lock1 = redissonClient1.getLock("myLock");
        RLock lock2 = redissonClient2.getLock("myLock");
        RLock lock3 = redissonClient3.getLock("myLock");
        lock1 = redissonClient1.getMultiLock(lock1,lock2,lock3);

        // 尝试得到（阻塞方法 等待最大时间 自动释放时间 时间单位）
        boolean isLock = lock1.tryLock(1, 10, TimeUnit.SECONDS);
        if (isLock) {
            try {
                System.out.println("in");
            } finally {
                // 释放锁
                lock1.unlock();
            }
        }
        return "Ok!";
    }

}
