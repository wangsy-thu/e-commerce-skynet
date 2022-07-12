package edu.neu.ecommerce.crawler.service;

import java.io.IOException;

public interface ResultSaveService {

    /**
     * <h2>根据关键字爬取网站SKU信息</h2>
     * 爬取结果存入ElasticSearch
     * @param keyword 爬取关键字
     */
    void crawSkuFromWeb(String keyword);
}
