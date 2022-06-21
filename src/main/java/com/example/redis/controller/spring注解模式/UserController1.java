package com.example.redis.controller.spring注解模式;

import com.example.redis.domain.Person;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/1")
public class UserController1 {

    public static final String CACHE_OBJECT = "demo";

    //Spring使用redis缓存：
    //因为缓存注解默认使用SPEL表达式，如果我们想使用字符串需要加上斜杠。
    public static final String CACHE_LIST_KEY = "\"list\"";

    // ########################### 增 ###########################

    /**
     * value属性指定Cache名称，如果名称相同，会调用同一个cache
     */
    @Cacheable(value = "demo1")
    @GetMapping("/0/{id}")
    public Person get0(@PathVariable("id") String id) {
        System.out.println(id);
        return null;
    }

    @Cacheable(value = "demo1")
    @GetMapping("/1/{id}")
    public Person get(@PathVariable("id") String id) {
        System.out.println(id);
        return new Person(id, "xxx");
    }

    @Cacheable(value = "demo1")
    @GetMapping("/2/{id}")
    public Person get2(@PathVariable("id") String id) {
        System.out.println(id);
        return new Person(id, "xxx");
    }

    /**
     * 加锁查询，SpringCache采用sync属性，只有一个线程去维护缓存，其他线程会被阻塞，直到缓存中更新该条目为止。
     * 也就是第一次查询只允许一个线程，等数据被缓存之后，才支持并发。
     */
    @Cacheable(value = "demo2", sync = true)
    @GetMapping("/3/{id}")
    public Person get3(@PathVariable("id") String id) {
        System.out.println(3);
        return new Person(id, "xxx");
    }

    /**
     * key demo
     * value 查询条件id
     */
    @Cacheable(value = CACHE_OBJECT, key = "#id")
    @GetMapping("/4/{id}")
    public Person getArticle(@PathVariable("id") String id) {
        System.out.println(4);
        return new Person(id, "xxx");
    }

    @Cacheable(value = CACHE_OBJECT, key = "#id")
    @GetMapping("/5/{id}")
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
            @CacheEvict(value = CACHE_OBJECT, key = CACHE_LIST_KEY),//删除List集合缓存
            @CacheEvict(value = CACHE_OBJECT, key = "#id")//删除单条记录缓存
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
    @CachePut(value = CACHE_OBJECT, key = "#article.getId()")
    @CacheEvict(value = CACHE_OBJECT, key = CACHE_LIST_KEY)
    @GetMapping("/7/{id}")
    public List<Person> update(@PathVariable("id") String id) {
        System.out.println(6);
        List<Person> list = new ArrayList<>();
        list.add(new Person(id, "1"));
        list.add(new Person(id, "2"));
        list.add(new Person(id, "3"));
        return list;
    }

}

