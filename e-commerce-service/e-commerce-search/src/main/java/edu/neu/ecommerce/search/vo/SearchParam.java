package edu.neu.ecommerce.search.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <h1>检索条件值对象定义</h1>
 * 封装页面所有可能传递过来的查询条件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchParam {

    /* 全文匹配关键字 */
    private String keyword;

    /* 三级分类ID */
    private Long catalog3Id;

    /* 排序条件 */
    private String sort;

    /*
    过滤条件：
    1，是否有货
    2，价格区间
    3，品牌选择
    4，属性
     */

    /* 是否只显示有货 */
    private Integer hasStock;

    /* 价格区间 */
    private String skuPrice;

    /* 选择品牌(支持多选) */
    private List<Long> brandId;

    /* 按照属性进行筛选 */
    private List<String> attrs;

    /* 传递的页码 */
    private Integer pageNum = 1;

    /* 原生所有查询条件 */
    private String _queryString;
}
