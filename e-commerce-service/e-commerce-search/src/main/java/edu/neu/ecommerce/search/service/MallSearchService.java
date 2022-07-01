package edu.neu.ecommerce.search.service;


import edu.neu.ecommerce.search.vo.SearchParam;
import edu.neu.ecommerce.search.vo.SearchResult;

public interface MallSearchService {

    /**
     * <h2>检索函数</h2>
     * @param param 检索的所有参数
     * @return 检索的结果
     */
    SearchResult search(SearchParam param);
}
