package edu.neu.ecommerce.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <h1>二级分类值对象定义</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Catelog2Vo {

    /* 父分类ID */
    private String catalog1Id;

    /* 子分类列表 */
    private List<Catelog3Vo> catalog3List;

    /* 二级分类ID */
    private String id;

    /* 二级分类名称 */
    private String name;

    /**
     * <h2>三级分类值对象定义</h2>
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Catelog3Vo{

        /* 二级分类ID */
        private String catalog2Id;

        /* 三级分类ID */
        private String id;

        /* 三级分类名称 */
        private String name;
    }
}
