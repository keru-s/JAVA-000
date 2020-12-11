# 学习笔记

## 周四-第二题

设计对前面的订单表数据进行水平分库分表，拆分 2 个库，每个库 16 张表。并在新结构在演示常见的增删改查操作。代码、sql 和配置文件。

### SQL

```sql
create DATABASE test;
CREATE TABLE `test`.`t_order_0` (
                         `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
                         `serial_num` bigint(20) unsigned NOT NULL COMMENT '流水号',
                         `state` int(5) DEFAULT NULL COMMENT '状态',
                         `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
                         `good_id` bigint(20) DEFAULT NULL COMMENT '商品id',
                         `total_price` decimal(10,2) DEFAULT NULL COMMENT '总价',
                         `address` varchar(500) DEFAULT NULL COMMENT '收货地址',
                         `create_tm` datetime DEFAULT NULL COMMENT '创建时间',
                         `modify_tm` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单';

create table `test`.`t_order_1` like `test`.`t_order_0`;
create table `test`.`t_order_2` like `test`.`t_order_0`;
create table `test`.`t_order_3` like `test`.`t_order_0`;
create table `test`.`t_order_4` like `test`.`t_order_0`;
create table `test`.`t_order_5` like `test`.`t_order_0`;
create table `test`.`t_order_6` like `test`.`t_order_0`;
create table `test`.`t_order_7` like `test`.`t_order_0`;
create table `test`.`t_order_8` like `test`.`t_order_0`;
create table `test`.`t_order_9` like `test`.`t_order_0`;
create table `test`.`t_order_10` like `test`.`t_order_0`;
create table `test`.`t_order_11` like `test`.`t_order_0`;
create table `test`.`t_order_12` like `test`.`t_order_0`;
create table `test`.`t_order_13` like `test`.`t_order_0`;
create table `test`.`t_order_14` like `test`.`t_order_0`;
create table `test`.`t_order_15` like `test`.`t_order_0`;
```

### 配置文件

```yaml
spring:
  shardingsphere:
    props:
      # 显示具体sql查询情况
      sql-show: true
    datasource:
      names: ds0,ds1
      # 通用配置
      common:
        type: com.zaxxer.hikari.HikariDataSource
        driver-class-name: com.mysql.cj.jdbc.Driver
        username: root
        password:
      ds0:
        jdbc-url: jdbc:mysql://localhost:3306/test?characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
      ds1:
        jdbc-url: jdbc:mysql://localhost:3316/test?characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    rules:
      sharding:
        default-database-strategy:
          standard:
            sharding-column: id
            sharding-algorithm-name: database-inline
        tables:
          t_order:
            # 主键算法
            key-generate-strategy:
              column: id
              key-generator-name: snowflake
            # 配置表规则
            actual-data-nodes: ds$->{0..1}.t_order_$->{0..16}
            # 分库策略
            database-strategy:
              standard:
                sharding-column: user_id
                sharding-algorithm-name: database-inline
            # 分表策略
            table-strategy:
              standard:
                sharding-column: id
                sharding-algorithm-name: table-inline
        # 分片算法配置
        sharding-algorithms:
          database-inline:
            type: INLINE
            props:
              algorithm-expression: ds$->{user_id % 2}
          table-inline:
            type: INLINE
            props:
              algorithm-expression: t_order_$->{id % 16}
        # 键自增算法配置
        key-generators:
          snowflake:
            type: SNOWFLAKE
            props:
              worker-id: 123
              max-vibration-offset: 15
```

### 代码

```java
    @Test
    public void testSave() {
        Random random = new Random();
        final Date date = new Date();

        for (int i = 0; i < 10_00; i++) {
            final Order order = new Order();
            order.setState(1);
            order.setCreateTm(date);
            order.setUserId((long) (Math.random() * 1024));
            order.setSerialNum(123L+random.nextInt(1000000));
            orderRepository.save(order);
        }

    }
```

查看插入结果：

 ` select table_name,table_rows from information_schema.tables where TABLE_SCHEMA = 'test';`

```bash
+------------+------------+
| table_name | table_rows |
+------------+------------+
| t_order_0  |         32 |
| t_order_1  |         31 |
| t_order_10 |         25 |
| t_order_11 |         37 |
| t_order_12 |         28 |
| t_order_13 |         27 |
| t_order_14 |         29 |
| t_order_15 |         32 |
| t_order_2  |         28 |
| t_order_3  |         30 |
| t_order_4  |         31 |
| t_order_5  |         24 |
| t_order_6  |         32 |
| t_order_7  |         36 |
| t_order_8  |         33 |
| t_order_9  |         26 |
+------------+------------+
```

可以看到，通过该配置，数据能均匀的落在不同的分片上。



## 周六-第一题

列举常见的分布式事务，简单分析其使用场景和优缺点。

常见的分布式事务有：

- 强一致：XA

- 弱一致：柔性事务，使用一套事务框架保证最终一致的事务。
  - TCC
  - AT

### XA

直接像单机数据库事务一样，多个数据库自动通过某种协调机制，实现了跨数据库节点的一致性。

**使用场景**：要求严格的一致性，比如金融交易类业务。

**优点**：市面上主流的数据库和消息中间件均基于XA协议做了实现，可以无缝对各种数据库和中间件进行整合。而且使用起来方便，对业务无入侵。

**缺点**：

1. 同步阻塞问题 (一般情况下，不需要调高隔离级别，XA默认不会改变隔离级别)
   全局事务内部包含了多个独立的事务分支，这一组事务分支要不都成功，要不都失败。各个事务分支的ACID特性共同构成了全局事务的ACID特性。
   也就是将单个事务分支的支持的ACID特性提升一个层次（up a level）到分布式事务的范畴。即使在非分布事务中（即本地事务），如果对操作读很敏感，我们也需要将事务隔离级别设置为SERIALIZABLE，而对于分布式事务来说，更是如此，可重复读隔离级别不足以保证分布式事务一致性。
   也就是说，如果我们使用mysql来支持XA分布式事务的话，那么最好将事务隔离级别设置为SERIALIZABLE，地球人都知道SERIALIZABLE（串行化）是四个事务隔离级别中最高的一个级别，也是执行效率最低的一个级别

2. 单点故障
   **成熟的XA框架需要考虑TM的高可用性**
   由于协调者的重要性，一旦协调者TM发生故障，参与者RM会一直阻塞下去。尤其在第二阶段，协调者发生故障，那么所有的参与者还都处于锁定事务资源的状态中，而无法继续完成事务操作。（如果是协调者挂掉，可以重新选举一个协调者，但是无法解决因为协调者宕机导致的参与者处于阻塞状态的问题）

3. 数据不一致

   **极端情况下，一定有事务失败问题，需要监控和人工处理**

   在二阶段提交的阶段二中，当协调者向参与者发送commit请求之后，发生了局部网络异常或者在发送commit请求过程中协调者发生了故障，这回导致只有一部分参与者接受到了commit请求。而在这部分参与者接到commit请求之后就会执行commit操作。但是其他部分未接到commit请求的机器则无法执行事务提交。于是整个分布式系统便出现了数据不一致性的现象。



### TCC

TCC模式即将每个服务业务操作分为两个阶段，第一个阶段检查并预留相关资源，第二阶段根据所有服务业务的Try状态来操作，如果都成功，则进行Confirm操作，如果任意一个Try发生错误，则全部Cancel。

适用场景：准实时或非实时的处理，比如T+1的各类操作，或者电商类操作。

优点：TCC 不依赖 RM 对分布式事务的支持，而是通过对业务逻辑的分解来实现分布式事务。

缺点：需要自行定义各个阶段的逻辑，对业务有侵入。



### AT

AT 模式就是两阶段提交，自动生成反向SQL，并保存到单独的数据库中。当需要回滚时，通过数据库内的数据和现场，就可以拼接出 revert SQL 进行回滚。

适用场景：准实时或非实时的处理，比如T+1的各类操作，或者电商类操作。

优点：可自动生成SQL，对业务侵入性小。

缺点：由于是自动生成SQL，执行效率会变慢，有可能出回滚错误的情况。



## 周六-第二题

基于 hmily TCC 或 ShardingSphere 的 Atomikos XA 实现一个简单的分布式事务应用 demo（二选一）。