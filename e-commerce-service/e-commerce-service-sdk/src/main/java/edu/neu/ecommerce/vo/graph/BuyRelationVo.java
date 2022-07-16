package edu.neu.ecommerce.vo.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>购买关系值对象定义</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuyRelationVo {

    /* 关联用户ID */
    private Long userId;

    /* 关联skuId */
    private Long skuId;

    /* 购买数量 */
    private Long buyCount;

    /* 购买商品 */
    private ProductNodeVo productNodeVo;

    /* 购买者 */
    private UserNodeVo userNodeVo;
}
