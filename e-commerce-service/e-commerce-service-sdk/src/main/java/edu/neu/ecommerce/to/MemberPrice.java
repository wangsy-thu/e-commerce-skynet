/**
  * Copyright 2022 bejson.com 
  */
package edu.neu.ecommerce.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberPrice {

    private Long id;
    private String name;
    private BigDecimal price;
}