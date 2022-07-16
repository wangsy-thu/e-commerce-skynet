package edu.neu.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <h1>点击事件统计值对象定义</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkuClickCountVo implements Serializable {

    /* SkuId */
    private Long skuId;

    /* 商品点击量 */
    private Long count;

    /* 统计窗口开始时间 */
    private Long windowStart;

    /* 统计窗口结束时间 */
    private Long windowEnd;
}
