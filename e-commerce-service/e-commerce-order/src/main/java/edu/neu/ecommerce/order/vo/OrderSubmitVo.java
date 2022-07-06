package edu.neu.ecommerce.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderSubmitVo {

    /** 收获地址的id **/
    private Long addrId;
    /** 支付方式 **/
    private Integer payType;
    //无需提交要购买的商品，去购物车再获取一遍
    //优惠、发票
    /** 防重令牌 **/
    private String orderToken;
    /** 应付价格 **/
    private BigDecimal payPrice;
}
