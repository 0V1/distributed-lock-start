package com.hermes.distributedlockstart.api;

import com.hermes.distributedlockstart.spi.DistributedLockSpi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author qinfeng
 * @date 2020/1/1
 */
@RestController
public class test {

    @Autowired
    DistributedLockSpi redisLockImpl;


    @GetMapping("/lock/{lockKey}/{lockValue}")
    public boolean testLock(@PathVariable("lockKey") String lockKey, @PathVariable("lockValue") String lockValue) {
        return redisLockImpl.tryLock(lockKey, lockValue, 10, TimeUnit.MINUTES);
    }

    @GetMapping("/unlock/{lockKey}/{lockValue}")
    public void testUnLock(@PathVariable("lockKey") String lockKey, @PathVariable("lockValue") String lockValue) {
        redisLockImpl.unLock(lockKey, lockValue);
    }


}
