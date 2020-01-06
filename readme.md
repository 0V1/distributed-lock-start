# 分布式锁的快速start

该项目实现了以下三种模式的分布式锁，实现原理基于各自特性，将通过统一接口调用，不同实现配合不同不配进行使用。
* redis：串行执行特性+数据过期策略
* zookeeper：临时目录唯一性+临时目录特性
* mysql：行锁+事务提交特性
## 分布式锁特性
* 互斥性：任意时刻只有一个对象持有锁
* 加解锁的唯一操作：解锁的动作只有持有锁的对象才能执行，避免自己持有的锁被别人解锁
* 非阻塞性：各个对象尝试去加锁，加锁成功则继续操作，加锁失败则跳出，中断后续操作
* 避免死锁：持有锁的对象出现问题而不能及时解锁时，需要自动解锁机制
 
## redis
redis的单线程执行特性将决定执行顺序，从而保证加锁的可靠性，解锁使用删除数据即可，如果锁异常占用过长时间将通过数据过期配置，进行锁的自动释放从而避免死锁问题。  

* [redis 官网 SETNX key value 解释](http://www.redis.cn/commands/setnx.html)   
    

    SETNX key value
    起始版本：1.0.0
    时间复杂度：O(1)
    将key设置值为value，如果key不存在，这种情况下等同SET命令。 当key存在时，什么也不做。SETNX是”SET if Not eXists”的简写。
    返回值
    Integer reply, 特定值:
    1 如果key被设置了
    0 如果key没有被设置
* [redis 执行lua 脚本](https://docs.spring.io/spring-data/redis/docs/2.2.3.RELEASE/reference/html/#scripting)  

checkandset.lua
~~~
local current = redis.call('GET', KEYS[1])
if current == ARGV[1]
  then redis.call('SET', KEYS[1], ARGV[2])
  return true
end
return false
~~~
redisTemplate.execute
~~~
public boolean checkAndSet(String expectedValue, String newValue) {
    ScriptSource scriptSource = new ResourceScriptSource(new ClassPathResource("META-INF/scripts/checkandset.lua");
    RedisScript<Boolean> script = RedisScript.of(scriptSource, Boolean.class);
    return redisTemplate.execute(script, singletonList("key"), asList(expectedValue, newValue));
  }
~~~
#### 小结
    redis 作为的分布式锁，简单高效，稳定，集群是可扩展至集群模式(Redisson)
---
## zookeeper
使用zk的临时节点特性来实现，分布式锁
* zk中的结构是由节点所组成，表现为文件目录的形式
* 临时节点：客户端可以建立一个临时节点，全局唯一性，在会话结束时自动删除
* 子节点：可以指定一个父节点，在下面创建子节点，这个时候父节点就相当于资源，临时子节点就相当于锁   

    
#### Curator分布式锁实现

---
## mysql
使用悲观锁（阻塞锁），select for update/commit
    
    mysql中锁分为乐观锁和悲观锁，乐观锁使用update version控制，悲观锁使用行锁或者表锁控制，
    在这里选用行锁，一行信息即为一个资源，一旦有任一链接执行成功即视为获取锁，
    其他链接执行将被阻塞，那么这里就需要设置一个查询超时，使其他没有获取到锁的链接及时释放，
    不至于一直等待执行，同时也需要一个try catch来捕获查询超时的异常作为获取锁失败的标志。
    这样使用有一个好处，就是不容易出现死锁，一旦事务执行完毕 必然释放锁，如果有任何异常导致链接断开
    也会自动释放锁。
####注意点：
* 需要手动开启事务，手动提交，链接断开后自动自动释放事务
* select for update 和 commit 一定要成对，手动事务容易形成开闭操作，那么就会有死锁
* 一旦出现死锁最简单办法即使重启大法

