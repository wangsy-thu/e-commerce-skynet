package edu.neu.ecommerce.crawler.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <h1>爬虫爬取数据文档对象定义</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkuCrawDoc {

    /* ID */
    private String id;

    /* 平台名称 */
    private String platformName;

    /* 商品名称 */
    private String skuName;

    /* 商品价格 */
    private BigDecimal skuPrice;

    /* 数据抓取时间 */
    private Date crawTime;

    /* 店铺名称 */
    private String shopName;

    /* 商品图片地址 */
    private String skuImgUrl;

    /* 评论数量 */
    private Integer commentNum;
}
