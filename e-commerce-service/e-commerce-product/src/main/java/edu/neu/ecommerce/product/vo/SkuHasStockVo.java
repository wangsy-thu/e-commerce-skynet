package edu.neu.ecommerce.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkuHasStockVo {

    /* sku 的id */
    private Long skuId;

    /* 是否有库存 */
    private Boolean hasStock;
}
