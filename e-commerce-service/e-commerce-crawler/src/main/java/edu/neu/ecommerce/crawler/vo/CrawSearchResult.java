package edu.neu.ecommerce.crawler.vo;

import edu.neu.ecommerce.crawler.document.SkuCrawDoc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <h1>检索结果值对象定义</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrawSearchResult {
    /* 检索结果 */
    private List<SkuCrawDoc> products;

    /* 当前页码 */
    private Integer page;

    /* 总记录条数 */
    private Long total;

    /* 页码总数 */
    private Integer totalPage;
}
