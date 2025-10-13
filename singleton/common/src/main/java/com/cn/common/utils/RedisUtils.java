package com.cn.common.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * redis工具类
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 */
@Component
@SuppressWarnings("all")
@RequiredArgsConstructor
public final class RedisUtils {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisTemplate getRedisTemplate() {
        return this.redisTemplate;
    }

    public Set<String> keys(String key) {
        return redisTemplate.keys(key);
    }

    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    public Long getExpire(final String key) {
        return redisTemplate.getExpire(key);
    }

    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        Boolean ret = redisTemplate.expire(key, timeout, unit);
        return ret != null && ret;
    }

    public boolean hasKey(final String key) {
        return redisTemplate.hasKey(key);
    }

    public long increment(final String key, final long delta) {

        return redisTemplate.opsForValue().increment(key, delta);
    }

    public double increment(final String key, final double delta) {

        return redisTemplate.opsForValue().increment(key, delta);
    }

    public boolean delKey(final String key) {
        Boolean ret = redisTemplate.delete(key);
        return ret != null && ret;
    }

    public long delKeys(final Collection<String> keys) {
        Long ret = redisTemplate.delete(keys);
        return ret == null ? 0 : ret;
    }

    public void setValue(final String key, final Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setValueTimeout(final String key, final Object value, final long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    public Object getValue(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean hasHashKey(final String key, String hkey) {
        Boolean ret = redisTemplate.opsForHash().hasKey(key, hkey);
        return ret != null && ret;
    }

    public long hashSize(final String key) {
     return redisTemplate.opsForHash().size(key);
    }

    public void hashPut(final String key, final String hKey, final Object value) {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    public void hashPutAll(final String key, final Map<String, Object> values) {
        redisTemplate.opsForHash().putAll(key, values);
    }

    public Object hashGet(final String key, final String hKey) {
        return redisTemplate.opsForHash().get(key, hKey);
    }


    public Map<Object, Object> hashGetAll(final String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public Map<Object, Object> hashMultiGet(final String key, final Collection<Object> fields) {
        List<Object> values = redisTemplate.opsForHash().multiGet(key, fields);
        Map<Object, Object> result = new HashMap<>();
        Object[] fieldArray = fields.toArray();
        for (int i = 0; i < fieldArray.length; i++) {
            if (values.get(i) != null) {
                result.put(fieldArray[i], values.get(i));
            }
        }
        return result;
    }

    public List<Object> hashMultiGetList(final String key, final Collection<Object> hKeys) {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    public boolean hashExists(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }


    public long hashDeleteKeys(final String key, final Collection<Object> hKeys) {
        return redisTemplate.opsForHash().delete(key, hKeys);
    }

    public Long hashDelete(final String key, final Object... hashKey) {
        return redisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * Hash 自增
     * @param key 键
     * @param hKey Hash 键
     * @param delta 增量
     * @return 增量后的值
     */
    public Long hashIncrement(final String key, final String hKey, final long delta) {
        return redisTemplate.opsForHash().increment(key, hKey, delta);
    }

    /**
     * Hash 自增（Double 类型）
     * @param key 键
     * @param hKey Hash 键
     * @param delta 增量
     * @return 增量后的值
     */
    public Double hashIncrement(final String key, final String hKey, final double delta) {
        return redisTemplate.opsForHash().increment(key, hKey, delta);
    }

    /**
     * 设置值（带过期时间）
     * @param key 键
     * @param value 值
     * @param timeout 过期时间（秒）
     */
    public void set(final String key, final Object value, final long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置值（不过期）
     * @param key 键
     * @param value 值
     */
    public void set(final String key, final Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 获取值
     * @param key 键
     * @return 值
     */
    public Object get(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public long setSet(final String key, final Object... values) {
        Long count = redisTemplate.opsForSet().add(key, values);
        return count == null ? 0 : count;
    }


    public long setDel(final String key, final Object... values) {
        Long count = redisTemplate.opsForSet().remove(key, values);
        return count == null ? 0 : count;
    }


    public Set<Object> getSetAll(final String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public long zsetSetAll(final String key, final Set<ZSetOperations.TypedTuple<Object>> values) {
        Long count = redisTemplate.opsForZSet().add(key, values);
        return count == null ? 0 : count;
    }

    public Double zsetSetGetSource(final String key, final Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    public Double zsetIncrementScore(final String key, final Object value, final Double increment) {
        return redisTemplate.opsForZSet().incrementScore(key, value, increment);
    }

    public Boolean zsetSet(final String key, final Object values, final Double source) {
        final Boolean add = redisTemplate.opsForZSet().add(key, values, source);
        return add;
    }


    public Set<ZSetOperations.TypedTuple<Object>> zsetReverseRangeWithScores(final String key, final Long start, final Long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }

    public Set<Object> zsetReverseRange(final String key, final Long start, final Long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    public long selfIncrease(final String key) {
        return redisTemplate.execute(new SessionCallback<Long>() {
            @Override
            public Long execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                Long count = operations.opsForValue().increment(key);
                operations.exec();
                return count;
            }
        });
    }

    public Double selfIncreaseSource(final String key, final Object value) {
        return redisTemplate.execute(new SessionCallback<Double>() {
            @Override
            public Double execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                Double count = operations.opsForZSet().incrementScore(key, value, 1);
                operations.exec();
                return count;
            }
        });
    }

    public long zsetDelAll(final String key, final Set<ZSetOperations.TypedTuple<Object>> values) {
        Long count = redisTemplate.opsForZSet().remove(key, values);
        return count == null ? 0 : count;
    }

    public long zsetDel(final String key, Object values) {
        Long count = redisTemplate.opsForZSet().remove(key, values);
        return count == null ? 0 : count;
    }

    public long listPush(final String key, final Object value) {
        Long count = redisTemplate.opsForList().leftPush(key, value);
        return count == null ? 0 : count;
    }

    public Object listPop(final String key, final int count, final TimeUnit unit) {
        return redisTemplate.opsForList().rightPop(key, count, unit);

    }

    public Boolean doesItExist(final String key) {
        return redisTemplate.hasKey(key);
    }

    public long listPushAll(final String key, final Collection<Object> values) {
        Long count = redisTemplate.opsForList().rightPushAll(key, values);
        return count == null ? 0 : count;
    }

    public long listPushAll(final String key, final Object... values) {
        Long count = redisTemplate.opsForList().rightPushAll(key, values);
        return count == null ? 0 : count;
    }

    /**
     * 向有序集合添加元素
     * @param key 键
     * @param value 值
     * @param score 分数（排序权重）
     * @return 是否添加成功
     */
    public Boolean zsetAdd(final String key, final Object value, final double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 从有序集合中删除元素
     * @param key 键
     * @param values 要删除的值
     * @return 删除的元素数量
     */
    public Long zsetRemove(final String key, final Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 按分数倒序获取有序集合中指定范围的元素（分页）
     * @param key 键
     * @param start 开始位置（从0开始）
     * @param end 结束位置
     * @return 元素列表
     */
    public Set<Object> zsetReverseRange(final String key, final long start, final long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 获取有序集合的元素总数
     * @param key 键
     * @return 元素总数
     */
    public Long zsetCount(final String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    public List<Object> listGet(final String key, final int start, final int end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    public Long keySize(final String key) {
        return redisTemplate.opsForList().size(key);
    }

    public Long keySetSize(final String key) {
        return redisTemplate.opsForSet().size(key);
    }
}
