package edu.neu.ecommerce.search.vo;

import edu.neu.ecommerce.to.es.SkuEsModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>检索结果值对象定义</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {

    /* 查询到的所有商品信息 */
    private List<SkuEsModel> products;

    /*
    查询到的分页信息
     */

    /* 当前页码 */
    private Integer pageNum;

    /* 总记录数 */
    private Long total;

    /* 总页码 */
    private Integer totalPages;

    /* 当前查询结果所有涉及到的品牌 */
    private List<BrandVo> brands;

    /* 当前查询结果所有涉及到的属性 */
    private List<AttrVo> attrs;

    /* 当前查询到的结果涉及到的所有分类 */
    private List<CatalogVo> catalogs;

    /* 分页导航页码 */
    private List<Integer> pageNavs;

    /* 面包屑导航数据 */
    private List<NavVo> navs = new ArrayList<>();

    private List<Long> attrIds = new ArrayList<>();

    //=========以上是返回给页面的所有信息===========

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NavVo{
        /* 导航名字 */
        private String navName;

        /* 导航值 */
        private String navValue;

        /* 跳转目标 */
        private String link;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrandVo{

        /* 品牌ID */
        private Long brandId;

        /* 品牌名称 */
        private String brandName;

        /* 品牌图片信息 */
        private String brandImg;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttrVo{

        /* 属性ID */
        private Long attrId;

        /* 属性名称 */
        private String attrName;

        /* 属性值 */
        private List<String> attrValue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CatalogVo{

        /* 分类ID */
        private Long catalogId;

        /* 分类名称 */
        private String catalogName;
    }
}
