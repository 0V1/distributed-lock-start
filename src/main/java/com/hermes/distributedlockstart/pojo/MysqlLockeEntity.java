package com.hermes.distributedlockstart.pojo;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author qinfeng
 * @date 2020/1/1
 */
@Data
public class MysqlLockeEntity {

    private Integer id;

    private String lockKey;

    private String lockValue;

    private long lockTime;

    private Timestamp createTime;
}
