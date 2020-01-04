package com.hermes.distributedlockstart.api;

import com.hermes.distributedlockstart.impl.mysql.MysqlLockImpl;
import com.hermes.distributedlockstart.pojo.MysqlLockeEntity;
import com.hermes.distributedlockstart.spi.DistributedLockSpi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * @author qinfeng
 * @date 2020/1/1
 */
@RestController
public class test {

    @Autowired
    DistributedLockSpi redisLockImpl;

    @Autowired
    MysqlLockImpl mysqlLockImpl;

    @GetMapping("/lock/{lockKey}/{lockValue}")
    public boolean testLock(@PathVariable("lockKey") String lockKey, @PathVariable("lockValue") String lockValue) {
        return redisLockImpl.tryLock(lockKey, lockValue, 10, TimeUnit.MINUTES);
    }

    @GetMapping("/unlock/{lockKey}/{lockValue}")
    public void testUnLock(@PathVariable("lockKey") String lockKey, @PathVariable("lockValue") String lockValue) {
        redisLockImpl.unLock(lockKey, lockValue);
    }

    @PostMapping("/mysql/add")
    public void insertLockInfo(@RequestBody MysqlLockeEntity lockeEntity){
        mysqlLockImpl.insertLockInfo(lockeEntity);
    }

    @GetMapping("/mysql/lock/{lockKey}/{lockValue}")
    public boolean testMysqlLock(@PathVariable("lockKey") String lockKey, @PathVariable("lockValue") String lockValue) throws InterruptedException {
        boolean tryLock = mysqlLockImpl.tryLock(lockKey, lockValue, 10, TimeUnit.MINUTES);
        if(tryLock){
            TimeUnit.SECONDS.sleep(11L);
            mysqlLockImpl.unLock(lockKey, lockValue);
        }
        return tryLock;
    }


}
