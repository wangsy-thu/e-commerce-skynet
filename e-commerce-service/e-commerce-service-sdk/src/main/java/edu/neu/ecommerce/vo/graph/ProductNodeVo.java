package edu.neu.ecommerce.vo.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>产品节点值对象定义</h1>
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductNodeVo {

    /* 商品SKU id */
    private Long skuId;

    /* 商品名称 */
    private String skuName;

    /* 类别id */
    private Long catalogId;

    /* 类别名称 */
    private String catalogName;
}
