package com.hermes.distributedlockstart.impl.mysql;

import com.hermes.distributedlockstart.impl.mysql.mapper.MysqlLockMapper;
import com.hermes.distributedlockstart.pojo.MysqlLockeEntity;
import com.hermes.distributedlockstart.spi.DistributedLockSpi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * MySQL 的实现方式
 *
 * @author qinfeng
 * @date 2020/1/1
 */
@Slf4j
@Service
public class MysqlLockImpl implements DistributedLockSpi {

    @Autowired
    private MysqlLockMapper mysqlLockMapper;

    @Autowired
    private DataSourceTransactionManager dataSourceTransactionManager;

    @Autowired
    private TransactionDefinition transactionDefinition;



    /**
     * 用于存放所有分布式锁对应的事务，以便于释放锁使用
     */
    private ConcurrentHashMap<String, TransactionStatus> transactionMap=new ConcurrentHashMap<>();

    public void insertLockInfo(MysqlLockeEntity lockeEntity) {
        mysqlLockMapper.insertLockInfo(lockeEntity);
    }

    @Override
    public boolean tryLock(String lockKey, String lockValue, long timeout, TimeUnit unit) {
        log.info("---{}尝试给{}加锁---", lockValue, lockKey);
        log.info("事务超时时间{}",transactionDefinition.getTimeout());
        TransactionStatus transaction= dataSourceTransactionManager.getTransaction(transactionDefinition);
        try {
            MysqlLockeEntity where = new MysqlLockeEntity();
            where.setLockKey(lockKey);
            where.setLockValue(lockValue);
            // 设置有查询限时，如果获取不到锁则会发生查询超时，自动关闭链接，可以不用回滚事务
            MysqlLockeEntity mysqlLockeEntity = mysqlLockMapper.selectForUpdate(where);
            if(mysqlLockeEntity!=null){
                transactionMap.put(lockKey+lockValue,transaction);
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            log.error("{}", e);
            return false;
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
        TransactionStatus transaction = transactionMap.get(lockKey+lockValue);
        dataSourceTransactionManager.commit(transaction);
        transactionMap.remove(lockKey+lockValue);
        log.info("---{}请求释放锁{},成功！---", lockValue, lockKey);
//        //释放锁的lua脚本
//        String unLockLua = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//
//        //转化为可执行的RedisScript对象,注意返回值类型  redis 默认是Long 类型，用其他类型接受将会报错
//        RedisScript<Long> unLockScript = new DefaultRedisScript(unLockLua, Long.class);
//
//        Long execute = redisTemplate.execute(unLockScript, asList(lockKey), lockValue);
//
//        if (execute.equals(1L)) {
//            log.info("---{}请求释放锁{},成功！---", lockValue, lockKey);
//        } else {
//            log.info("---{}请求释放锁{},失败！---", lockValue, lockKey);
//        }
    }
}
