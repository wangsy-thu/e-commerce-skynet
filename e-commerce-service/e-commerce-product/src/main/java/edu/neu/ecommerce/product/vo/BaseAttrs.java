package edu.neu.ecommerce.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseAttrs {

    private Long attrId;
    private String attrValues;
    private int showDesc;
}