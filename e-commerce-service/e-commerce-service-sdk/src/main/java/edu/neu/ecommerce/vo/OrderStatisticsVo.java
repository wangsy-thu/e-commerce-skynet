package edu.neu.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * <h1>订单统计值对象定义</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatisticsVo {

    /* 类型ID 用于分组聚合 */
    private Long typeId;

    /* 订单成交价格 */
    private BigDecimal price;

    /* 订单成交时间 */
    private String timeStr;

}
