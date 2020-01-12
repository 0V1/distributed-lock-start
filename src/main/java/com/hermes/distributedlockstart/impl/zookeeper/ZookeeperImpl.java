package com.hermes.distributedlockstart.impl.zookeeper;

import com.hermes.distributedlockstart.spi.DistributedLockSpi;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author qinfeng
 * @date 2020/1/11
 */
@Service
public class ZookeeperImpl implements DistributedLockSpi {

    @Autowired
    private CuratorFramework curatorFramework;

    @Override
    public boolean tryLock(String lockKey, String lockValue, long timeout, TimeUnit unit) {
        try {
            curatorFramework.create()
                    .withTtl(timeout)
                    .creatingParentsIfNeeded()
                    // PERSISTENT_WITH_TTL 和 PERSISTENT_SEQUENTIAL_WITH_TTL 都是zk3.5.6新增的节点类型，增加了 TTL
                    // 通过配置持久化节点+TTL 就可以达到锁定时自动释放，这里锁信息唯一所以不需要顺序化
                    .withMode(CreateMode.PERSISTENT_WITH_TTL)
                    // ACL:Access Control List ZK的权限控制,此处设置为无权限
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
