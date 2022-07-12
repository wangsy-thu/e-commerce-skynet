package edu.neu.ecommerce.crawler.service;

import edu.neu.ecommerce.crawler.vo.CrawSearchParam;
import edu.neu.ecommerce.crawler.vo.CrawSearchResult;

public interface ResultSearchService {

    /**
     * <h2>爬取数据检索服务</h2>
     * @param param 检索参数
     * @return 搜索结果
     */
    CrawSearchResult search(CrawSearchParam param);
}
