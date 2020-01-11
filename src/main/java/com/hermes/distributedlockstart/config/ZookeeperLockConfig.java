package com.hermes.distributedlockstart.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qinfeng
 * @date 2020/1/1
 */
@Configuration
public class ZookeeperLockConfig {

    private String zkConnectionStr = "192.168.216.128";

    @Bean
    public CuratorFramework getCuratorFramework() {

        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3, Integer.MAX_VALUE);
        CuratorFramework builder = CuratorFrameworkFactory
                .builder()
                .namespace("/zookeeperLock")
                .connectString(zkConnectionStr)
                .retryPolicy(retryPolicy)
                // 防止死锁 链接或者会话超时后自动断开
                .sessionTimeoutMs(60000)
                .connectionTimeoutMs(15000)
                .build();
        builder.start();
        return builder;
    }
}
