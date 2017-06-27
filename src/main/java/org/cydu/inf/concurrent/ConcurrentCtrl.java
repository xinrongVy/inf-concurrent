package org.cydu.inf.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 锁定服务
 */
@Service("infConcurrentCtrl")
public class ConcurrentCtrl {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 默认过期时间（秒）
     */
    private static int DEFAULT_EXPIRE_TIME = 60;

    private static final String DEFAULT_VALUE = "1";

    /**
     * Redis模板
     */
    private RedisTemplate redisTemplate;

    /**
     * Redis键前缀
     */
    private String redisKeyPrefix = "com.tcl.iread.biz.concurrent-redis.lock:";

    public boolean isConcurrent(String sourceType, String source) {
        if (StringUtils.isEmpty(sourceType) || StringUtils.isEmpty(source)) {
            return false;
        }
        return isConcurrent(sourceType, source, DEFAULT_EXPIRE_TIME);
    }

    public boolean isConcurrent(String sourceType, String source, int expiredSeconds) {
        if (StringUtils.isEmpty(sourceType) || StringUtils.isEmpty(source)) {
            return false;
        }

        String ctrlKey = generateCtrlKey(sourceType, source);

        Object value = redisTemplate.opsForValue().getAndSet(ctrlKey, DEFAULT_VALUE);
        redisTemplate.expire(ctrlKey, expiredSeconds, TimeUnit.SECONDS);

        return value != null;
    }

    public void removeConcurrentCtrl(String sourceType, String source) {
        String ctrlKey = generateCtrlKey(sourceType, source);
        redisTemplate.delete(ctrlKey);
    }

    private String generateCtrlKey(String sourceType, String source) {
        StringBuilder sb = new StringBuilder();
        sb.append(redisKeyPrefix);
        sb.append(sourceType);
        sb.append(":");
        sb.append(source);
        return sb.toString();
    }

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String getRedisKeyPrefix() {
        return redisKeyPrefix;
    }

    public void setRedisKeyPrefix(String redisKeyPrefix) {
        this.redisKeyPrefix = redisKeyPrefix;
    }
}
