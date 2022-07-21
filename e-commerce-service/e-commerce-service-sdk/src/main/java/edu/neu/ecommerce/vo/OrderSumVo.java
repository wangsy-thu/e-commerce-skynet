package edu.neu.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * <h1>订单统计</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSumVo {

    /* 类别ID 用于分组 */
    private Long typeId;

    /* 窗口内销售总额 */
    private BigDecimal totalPrice;

    /* 窗口开始时间 */
    private Long windowStart;

    /* 窗口结束时间 */
    private Long windowEnd;
}
