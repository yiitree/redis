package com.example.redis.controller.代码模式;

import com.example.redis.domain.Person;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/2")
public class UserController {

    @Resource
    private RedisTemplate<String,String> redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/set")
    public String set() {
        stringRedisTemplate.opsForValue().set("one", "1");

        // redisTemplate 保存的是字节序列，因为 RestTemplateConfig 自定义的时候指定了 key 和 value 的序列化器。
        redisTemplate.opsForValue().set("two", "2");
        redisTemplate.opsForValue().set("person", new Person("luffy", "123456789").toString());

        // 测试线程安全
        //        ExecutorService executorService = Executors.newFixedThreadPool(1000);
        //        IntStream.range(0, 1000).forEach(i -> {
        //            executorService.execute(() -> stringRedisTemplate.opsForValue().increment("num", 1));
        //        });
        return "Ok!";
    }

    @GetMapping("/get")
    public String get() {
        String one = stringRedisTemplate.opsForValue().get("one");
        if ("1".equals(one)) {
            System.out.println("key: one" + " || value: " + one);
        }

        Object two = redisTemplate.opsForValue().get("two");
        if ("2".equals(two.toString())) {
            System.out.println("key: two" + " || value: " + two);
        }

//        Person user = (Person) redisTemplate.opsForValue().get("person").toString();
//        if ("luffy".equals(user.getFirstname())) {
//            System.out.println("key: person" + " || value: " + user);
//        }
        return "Ok!";
    }

}

