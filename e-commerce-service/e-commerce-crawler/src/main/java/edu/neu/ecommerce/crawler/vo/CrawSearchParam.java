package edu.neu.ecommerce.crawler.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>爬虫数据检索参数</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrawSearchParam {

    /* 检索关键词 */
    private String keyword;

    /* 排序条件 */
    private String sort;

    /* 价格区间 */
    private String price;

    /* 页码编号 */
    private Integer page;

    /* 页长度 */
    private Integer size;
}
