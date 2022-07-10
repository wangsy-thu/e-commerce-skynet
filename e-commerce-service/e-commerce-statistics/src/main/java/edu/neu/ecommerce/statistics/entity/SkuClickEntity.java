package edu.neu.ecommerce.statistics.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <h1>商品点击实体类定义</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_click_log")
public class SkuClickEntity implements Serializable {
    /* 点击日志ID */
    @TableId
    private Long id;

    /* 商品SKU ID*/
    private Long skuId;

    /* 用户ID */
    private Long userId;

    /* 访问时间 */
    private LocalDateTime clickTime;
}
