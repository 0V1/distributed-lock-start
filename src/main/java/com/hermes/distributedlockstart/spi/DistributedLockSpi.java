package com.hermes.distributedlockstart.spi;

import java.util.concurrent.TimeUnit;

/**
 * 参考juc下的lock接口，这里提供两个主要方法，加锁和解锁
 *
 * @author qinfeng
 * @date 2020/1/1
 * @see java.util.concurrent.locks.Lock
 */
public interface DistributedLockSpi {

    /**
     * 尝试加锁，加锁成功则返回true，加锁失败则返回false
     *
     * @param lockKey   must not be {@literal null}. 锁的资源信息
     * @param lockValue must not be {@literal null}. 锁的持有者信息
     * @param timeout   must not be {@literal null}. 有效期
     * @param unit      must not be {@literal null}. 时间单位
     * @return boolean
     */
    boolean tryLock(String lockKey, String lockValue, long timeout, TimeUnit unit);

    /**
     * 解锁，只有加锁者自己才可以解锁
     *
     * @param lockKey   must not be {@literal null}. 锁的资源信息
     * @param lockValue must not be {@literal null}. 锁的持有者信息
     */
    void unLock(String lockKey, String lockValue);

}
