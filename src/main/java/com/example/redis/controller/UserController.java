package com.example.redis.controller;

import com.example.redis.domain.Person;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class UserController {

    @Cacheable(value="demo1")
    @GetMapping("/0/{id}")
    public Person get0(@PathVariable("id") String id){
        System.out.println(0);
        return null;
    }

    //Spring使用redis缓存：

    // value属性指定Cache名称，如果名称相同，会调用同一个cache
    @Cacheable(value="demo1")
    @GetMapping("/1/{id}")
    public Person get(@PathVariable("id") String id){
        System.out.println(1);
        return new Person(id,"xxx");
    }

    // value属性指定Cache名称
    @Cacheable(value="demo1")
    @GetMapping("/2/{id}")
    public Person get2(@PathVariable("id") String id){
        System.out.println(2);
        return new Person(id,"xxx");
    }

    // 第三种方法就是加锁，SpringCache采用sync属性，只有一个线程去维护缓存，其他线程会被阻塞，直到缓存中更新该条目为止。
    // 也就是第一次查询只允许一个线程，等数据被缓存之后，才支持并发。
    // value属性指定Cache名称
    @Cacheable(value="demo2",sync = true)
    @GetMapping("/3/{id}")
    public Person get3(@PathVariable("id") String id){
        System.out.println(3);
        return new Person(id,"xxx");
    }

    public static final String CACHE_OBJECT = "demo";
    //因为缓存注解默认使用SPEL表达式，如果我们想使用字符串需要加上斜杠。
    public static final String CACHE_LIST_KEY  = "\"list\"";

    @GetMapping("/4/{id}")
    @Cacheable(value = CACHE_OBJECT,key = "#id")   //这里的value和key参考下面的redis数据库截图理解
    public Person getArticle(@PathVariable("id") String id) {
        System.out.println(4);
        return new Person(id,"xxx");
    }

    @GetMapping("/5/{id}")
    @Cacheable(value = CACHE_OBJECT,key = "#id")
    public List<Person> getAll(@PathVariable("id") String id) {
        List<Person> list = new ArrayList<>();
        list.add(new Person(id, "1"));
        list.add(new Person(id, "2"));
        list.add(new Person(id, "3"));
        System.out.println(5);
        return list;
    }

    /**
     * 删除缓存
     */
    @Caching(evict = {
            @CacheEvict(value = CACHE_OBJECT,key = CACHE_LIST_KEY),//删除List集合缓存
            @CacheEvict(value = CACHE_OBJECT,key = "#id")//删除单条记录缓存
    })
    @GetMapping("/6/{id}")
    public List<Person> delete(@PathVariable("id") String id) {
        System.out.println(6);
        List<Person> list = new ArrayList<>();
        list.add(new Person(id, "1"));
        list.add(new Person(id, "2"));
        list.add(new Person(id, "3"));
        return list;
    }

    /**
     * 修改缓存
     */
    // 修改
    @CachePut(value = CACHE_OBJECT,key = "#article.getId()")
    // 删除查询list的
    @CacheEvict(value = CACHE_OBJECT,key = CACHE_LIST_KEY)
    @GetMapping("/7/{id}")
    public List<Person> update(@PathVariable("id") String id) {
        System.out.println(6);
        List<Person> list = new ArrayList<>();
        list.add(new Person(id, "1"));
        list.add(new Person(id, "2"));
        list.add(new Person(id, "3"));
        return list;
    }



    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/set")
    public String set() {
        stringRedisTemplate.opsForValue().set("one", "1");

        // redisTemplate 保存的是字节序列，因为 RestTemplateConfig 自定义的时候指定了 key 和 value 的序列化器。
        redisTemplate.opsForValue().set("two", "2");
        redisTemplate.opsForValue().set("person", new Person("luffy", "123456789"));

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

        Person user = (Person) redisTemplate.opsForValue().get("person");
        if ("luffy".equals(user.getFirstname())) {
            System.out.println("key: person" + " || value: " + user);
        }
        return "Ok!";
    }

}

