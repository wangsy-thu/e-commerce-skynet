package edu.neu.ecommerce.ware.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseItemDoneVO {

    private Long itemId;// 采购需求ID

    private Integer status;// 采购状态

    private String reason;// 原因

}
