package edu.neu.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * <h1>用户点击统计VO</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkuClickVo {

    /* 点击日志ID */
    private Long id;

    /* 商品SKU ID*/
    private Long skuId;

    /* 用户ID */
    private Long userId;

    /* 访问时间 */
    private String clickTime;
}
