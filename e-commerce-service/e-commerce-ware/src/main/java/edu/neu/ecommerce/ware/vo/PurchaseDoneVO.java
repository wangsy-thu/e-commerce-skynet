package edu.neu.ecommerce.ware.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDoneVO {

    @NotNull(message = "id不允许为空")
    private Long id;// 采购单ID

    private List<PurchaseItemDoneVO> items;// 采购需求

}
