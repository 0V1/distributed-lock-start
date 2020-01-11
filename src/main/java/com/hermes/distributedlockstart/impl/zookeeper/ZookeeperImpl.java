package com.hermes.distributedlockstart.impl.zookeeper;

import com.hermes.distributedlockstart.spi.DistributedLockSpi;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * @author qinfeng
 * @date 2020/1/11
 */
public class ZookeeperImpl implements DistributedLockSpi {

    @Autowired
    private CuratorFramework curatorFramework;

    @Override
    public boolean tryLock(String lockKey, String lockValue, long timeout, TimeUnit unit) {
        try {
            curatorFramework.create()
                    .withTtl(timeout)
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT_SEQUENTIAL_WITH_TTL)
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                    .forPath(lockKey, lockKey.getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void unLock(String lockKey, String lockValue) {

        try {
            if (curatorFramework.checkExists().forPath(lockKey) != null) {
                curatorFramework.delete().forPath(lockKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
