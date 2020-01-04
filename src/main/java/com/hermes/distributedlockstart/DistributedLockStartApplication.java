package com.hermes.distributedlockstart;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * start
 *
 * @author qinfeng
 * @deprecated 908247035@qq.com
 */
@SpringBootApplication
@MapperScan("com.hermes.distributedlockstart.impl.mysql.mapper")
public class DistributedLockStartApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributedLockStartApplication.class, args);
    }

}
