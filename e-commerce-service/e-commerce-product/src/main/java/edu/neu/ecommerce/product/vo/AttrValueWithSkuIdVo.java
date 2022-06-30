package edu.neu.ecommerce.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttrValueWithSkuIdVo {

    private String attrValue;
    private String skuIds;
}
