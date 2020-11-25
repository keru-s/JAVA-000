SET NAMES utf8mb4;


-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `username` varchar(40) DEFAULT NULL COMMENT '用户名',
  `account` varchar(40) DEFAULT NULL COMMENT '账号',
  `email` varchar(100) DEFAULT NULL COMMENT '电子邮箱',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `alipay_account` varchar(100) DEFAULT NULL COMMENT '支付宝账号',
  `bank_account` varchar(30) DEFAULT NULL COMMENT '银行账号',
  `phone` varchar(20) DEFAULT NULL COMMENT '电话号码',
  `address` varchar(500) DEFAULT NULL COMMENT '收货地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户';

DROP TABLE IF EXISTS `good`;
CREATE TABLE `good` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '商品主键',
  `good_name` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
  `stock` int(11) DEFAULT NULL COMMENT '库存数',
  `describe` varchar(1000) DEFAULT NULL COMMENT '商品描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品';


DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `serial_num` bigint(20) unsigned NOT NULL COMMENT '流水号',
  `state` int(5) DEFAULT NULL COMMENT '状态',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `good_id` bigint(20) DEFAULT NULL COMMENT '商品id',
  `total_price` decimal(10,2) DEFAULT NULL COMMENT '总价',
  `address` varchar(500) DEFAULT NULL COMMENT '收货地址',
  `waybill_no` varchar(50) DEFAULT NULL COMMENT '运单号',
  `express_company` varchar(50) DEFAULT NULL COMMENT '快递公司',
  `pay_type` varchar(10) DEFAULT NULL COMMENT '支付方式',
  `create_tm` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_tm` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单';