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