package com.hermes.distributedlockstart.impl.mysql.mapper;

import com.hermes.distributedlockstart.pojo.MysqlLockeEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * MysqlLockDao
 *
 * @author qinfeng
 * @date 2020/1/1
 */
@Mapper
public interface MysqlLockMapper {

    /**
     * 使用select for update 作为悲观锁，行锁的形式占用数据
     *
     * @param where where
     * @return MysqlLockeEntity
     */
    MysqlLockeEntity selectForUpdate(MysqlLockeEntity where);

    /**
     * 使用select for update 作为悲观锁，行锁的形式占用数据
     *
     * @param lock lock
     */
    void insertLockInfo(MysqlLockeEntity lock);

}
