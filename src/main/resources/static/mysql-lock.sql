create table `mysql-lock`.lock_info
(
    id          int auto_increment
        primary key,
    lock_key    varchar(100) not null comment '资源，即锁住的对象，全局唯一',
    lock_value  varchar(100) not null comment '锁的持有者',
    lock_time   mediumtext   null comment '有效时间',
    create_time timestamp    not null comment '创建时间',
    constraint lock_lock_key_uindex
        unique (lock_key)
);