package edu.neu.ecommerce.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attr {

    private Long attrId;
    private String attrName;
    private String attrValue;
}