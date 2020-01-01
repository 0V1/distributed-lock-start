package com.hermes.distributedlockstart.config;

/**
 * @author qinfeng
 * @date 2020/1/1
 */
public class DistributedLockConfig {

    private RedisLockConfig redisLock;

    private ZookeeperLockConfig zookeeperLock;

    private MySQLLockConfig mySQLLock;
}
