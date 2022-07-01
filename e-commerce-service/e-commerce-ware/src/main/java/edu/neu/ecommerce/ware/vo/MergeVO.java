package edu.neu.ecommerce.ware.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MergeVO {

    private Long purchaseId;// 采购单ID
    private List<Long> items;// 采购需求ID

}
