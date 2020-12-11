package com.ins.db.multiple.datasource.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 订单
 * @author 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_order")
public class Order implements Serializable {
    /**
     * 自增id
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 流水号
     */
    private Long serialNum;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 商品id
     */
    private Long goodId;

    /**
     * 总价
     */
    private BigDecimal totalPrice;

    /**
     * 收货地址
     */
    private String address;

    /**
     * 创建时间
     */
    private Date createTm;

    /**
     * 更新时间
     */
    private Date modifyTm;

    private static final long serialVersionUID = 1L;
}