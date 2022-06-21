package com.example.redis;

import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest
public class HotRankTest {

    @Resource
    public RedisTemplate<String,Object> redisTemplate;

    public static final String SCORE_RANK = "position_rank";

    /**
     * 批量新增
     */
    @Test
    public void batchAdd() {
        Set<ZSetOperations.TypedTuple<Object>> hotRankSet = new HashSet<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            DefaultTypedTuple<Object> tuple = new DefaultTypedTuple<>("张三" + i, 1D + i);
            hotRankSet.add(tuple);
        }
        System.out.println("循环时间:" +( System.currentTimeMillis() - start));
        Long num = redisTemplate.opsForZSet().add(SCORE_RANK, hotRankSet);
        System.out.println("批量新增时间:" +(System.currentTimeMillis() - start));
        System.out.println("受影响行数：" + num);
    }

    /**
     * 获取排行列表
     */
    @Test
    public void list() {
        Set<Object> range = redisTemplate.opsForZSet().reverseRange(SCORE_RANK, 0, 10);
        System.out.println("获取到的排行列表:" + JSONUtil.toJsonStr(range));
        Set<ZSetOperations.TypedTuple<Object>> rangeWithScores = redisTemplate.opsForZSet().reverseRangeWithScores(SCORE_RANK, 0, 10);
        System.out.println("获取到的排行和分数列表:" + JSONUtil.toJsonStr(rangeWithScores));
    }

    /**
     * 获取排行列表
     */
    @Test
    public void add() {
        redisTemplate.opsForZSet().add(SCORE_RANK, "李四", 1000000);
    }

    /**
     * 获取单个的排行
     */
    @Test
    public void find(){
        Long rankNum = redisTemplate.opsForZSet().reverseRank(SCORE_RANK, "李四");
        System.out.println("李四的个人排名：" + rankNum);

        Double score = redisTemplate.opsForZSet().score(SCORE_RANK, "李四");
        System.out.println("李四的分数:" + score);
    }

    /**
     * 统计两个分数之间的人数
     */
    @Test
    public void count(){
        Long count = redisTemplate.opsForZSet().count(SCORE_RANK, 8001, 9000);
        System.out.println("统计8001-9000之间的人数:" + count);
    }

    /**
     * 获取整个集合的基数(数量大小)
     */
    @Test
    public void zCard(){
        Long aLong = redisTemplate.opsForZSet().zCard(SCORE_RANK);
        System.out.println("集合的基数为：" + aLong);
    }

    /**
     * 使用加法操作分数
     */
    @Test
    public void incrementScore(){
        Double score = redisTemplate.opsForZSet().incrementScore(SCORE_RANK, "李四", 1000);
        System.out.println("李四分数+1000后：" + score);
    }

    /**
     * 在以上测试类中我们使用了redis的那些功能呢？在以上的例子中我们使用了单个新增，批量新增，获取前十，获取单人排名这些操作，但是redisTemplate还提供了更多的方法。
     *
     * 新增or更新
     *
     * 有三种方式，一种是单个，一种是批量，对分数使用加法(如果不存在，则从0开始加)。
     *
     * //单个新增or更新
     * Boolean add(K key, V value, double score);
     * //批量新增or更新
     * Long add(K key, Set<TypedTuple<V>> tuples);
     * //使用加法操作分数
     * Double incrementScore(K key, V value, double delta);
     *
     *
     * 删除
     *
     * 删除提供了三种方式：通过key/values删除，通过排名区间删除，通过分数区间删除。
     *
     * //通过key/value删除
     * Long remove(K key, Object... values);
     *
     * //通过排名区间删除
     * Long removeRange(K key, long start, long end);
     *
     * //通过分数区间删除
     * Long removeRangeByScore(K key, double min, double max);
     *
     *
     * 查
     *
     * 1.列表查询:分为两大类，正序和逆序。以下只列表正序的，逆序的只需在方法前加上reverse即可：
     *
     * //通过排名区间获取列表值集合
     *
     * Set<V> range(K key, long start, long end);
     *
     * //通过排名区间获取列表值和分数集合
     * Set<TypedTuple<V>> rangeWithScores(K key, long start, long end);
     *
     * //通过分数区间获取列表值集合
     * Set<V> rangeByScore(K key, double min, double max);
     *
     * //通过分数区间获取列表值和分数集合
     * Set<TypedTuple<V>> rangeByScoreWithScores(K key, double min, double max);
     *
     * //通过Range对象删选再获取集合排行
     * Set<V> rangeByLex(K key, Range range);
     *
     * //通过Range对象删选再获取limit数量的集合排行
     * Set<V> rangeByLex(K key, Range range, Limit limit);
     *
     *
     * 2.单人查询
     *
     * 可获取单人排行，和通过key/value获取分数。以下只列表正序的，逆序的只需在方法前加上reverse即可：
     *
     * //获取个人排行
     * Long rank(K key, Object o);
     *
     * //获取个人分数
     * Double score(K key, Object o);
     *
     *
     * 统计
     *
     * 统计分数区间的人数，统计集合基数。
     *
     * //统计分数区间的人数
     * Long count(K key, double min, double max);
     *
     * //统计集合基数
     * Long zCard(K key);
     */
}
