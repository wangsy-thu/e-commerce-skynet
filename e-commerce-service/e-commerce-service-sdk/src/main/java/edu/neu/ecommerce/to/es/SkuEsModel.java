package edu.neu.ecommerce.to.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * <h1>Sku的ES文档模型</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkuEsModel {

    /* sku的id*/
    private Long skuId;

    /* spu的id*/
    private Long spuId;

    /* spu的标题*/
    private String skuTitle;

    /* 商品价格 */
    private BigDecimal skuPrice;

    /* 默认图片 */
    private String skuImg;

    /* 销量 */
    private Long saleCount;

    /* 是否有货 */
    private Boolean hasStock;

    /* 热度评分 */
    private Long hotScore;

    /* 品牌ID */
    private Long brandId;

    /* 分类ID */
    private Long catalogId;

    /* 品牌名称 */
    private String brandName;

    /* 品牌图片 */
    private String brandImg;

    /* 分类名称 */
    private String catalogName;

    /* 属性 */
    private List<Attrs> attrs;

    /**
     * <h2>属性定义</h2>
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Attrs{

        /* 属性ID */
        private Long attrId;

        /* 属性名称 */
        private String attrName;

        /* 属性值 */
        private String attrValue;
    }
}
