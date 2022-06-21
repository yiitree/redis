package com.example.redis;

import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import javax.annotation.Resource;
import java.util.Set;

@SpringBootTest
public class HotRankTest1 {

    @Resource
    public RedisTemplate<String,Object> redisTemplate;

    public static final String SCORE_RANK = "position_rank";

    /**
     * 增加浏览量
     */
    @Test
    public void add() {
        String positionId = "111";
        // 如果没有数据会自动创建数据
        Boolean add = redisTemplate.opsForZSet().add(SCORE_RANK, positionId, 1);
        System.out.println(add);
    }

    /**
     * 删除
     */
    @Test
    public void delete(){
        String positionId = "111";
        redisTemplate.opsForZSet().remove(SCORE_RANK, positionId);
    }

    /**
     * 获取排行列表
     */
    @Test
    public void list() {
        // 获取职位id
        Set<Object> range = redisTemplate.opsForZSet().reverseRange(SCORE_RANK, 0, 9);
        System.out.println("获取职位列表:" + JSONUtil.toJsonStr(range));

        // 获取职位id和分数
        Set<ZSetOperations.TypedTuple<Object>> rangeWithScores = redisTemplate.opsForZSet().reverseRangeWithScores(SCORE_RANK, 0, 10);
        if (rangeWithScores != null) {
            for (ZSetOperations.TypedTuple<Object> rangeWithScore : rangeWithScores) {
                System.out.print("职位id:" + rangeWithScore.getValue());
                rangeWithScore.getValue();
                System.out.println(" 分数:" + rangeWithScore.getScore());
            }
        }
    }

    /**
     * 获取单职位情况
     */
    @Test
    public void find(){
        String positionId = "111";
        Long rankNum = redisTemplate.opsForZSet().reverseRank(SCORE_RANK, positionId);
        System.out.println("排名：" + rankNum);

        Double score = redisTemplate.opsForZSet().score(SCORE_RANK, positionId);
        System.out.println("点击数:" + score);
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
