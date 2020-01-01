package com.hermes.distributedlockstart.impl.redis;

import com.hermes.distributedlockstart.spi.DistributedLockSpi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;
import static org.springframework.data.redis.connection.ReactiveRedisConnection.RangeCommand.key;
import static org.springframework.data.redis.connection.ReactiveSetCommands.SDiffCommand.keys;

/**
 * redis 的实现方式
 *
 * @author qinfeng
 * @date 2020/1/1
 */
@Slf4j
@Service
public class RedisLockImpl implements DistributedLockSpi {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean tryLock(String lockKey, String lockValue, long timeout, TimeUnit unit) {

        log.info("---{}尝试给{}加锁---", lockValue, lockKey);

        //必须设定lock的有效期，否则出现崩溃等无法释放锁的场景时，将会陷入死锁状态
        Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, timeout, unit);

        if (Boolean.TRUE.equals(result)) {
            log.info("---{}尝试给{}加锁,成功！有效期:{}|{}---", lockValue, lockKey, timeout, unit);
            return Boolean.TRUE;
        } else {
            log.warn("---{}尝试给{}加锁,失败！---", lockValue, lockKey);
            return Boolean.FALSE;
        }
    }

    /**
     * 这里为了避免极端场景的错误释放锁，采用lua脚本将查询锁信息和释放锁两个原子操作封装在一起执行
     *
     * @param lockKey   must not be {@literal null}. 锁的资源信息
     * @param lockValue must not be {@literal null}. 锁的持有者信息
     */
    @Override
    public void unLock(String lockKey, String lockValue) {
        log.info("---{}请求释放锁{}---", lockValue, lockKey);

        //释放锁的lua脚本
        String unLockLua = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

        //转化为可执行的RedisScript对象,注意返回值类型  redis 默认是Long 类型，用其他类型接受将会报错
        RedisScript<Long> unLockScript = new DefaultRedisScript(unLockLua, Long.class);

        Long execute = redisTemplate.execute(unLockScript, asList(lockKey), lockValue);

        if (execute.equals(1L)) {
            log.info("---{}请求释放锁{},成功！---", lockValue, lockKey);
        } else {
            log.info("---{}请求释放锁{},失败！---", lockValue, lockKey);
        }
    }
}
