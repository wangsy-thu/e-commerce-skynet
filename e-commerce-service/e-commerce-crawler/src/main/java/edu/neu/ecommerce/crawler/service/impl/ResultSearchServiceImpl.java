package edu.neu.ecommerce.crawler.service.impl;

import com.alibaba.fastjson.JSON;
import edu.neu.ecommerce.crawler.document.SkuCrawDoc;
import edu.neu.ecommerce.crawler.service.ResultSearchService;
import edu.neu.ecommerce.crawler.vo.CrawSearchParam;
import edu.neu.ecommerce.crawler.vo.CrawSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ResultSearchServiceImpl implements ResultSearchService {

    private final RestHighLevelClient client;

    public ResultSearchServiceImpl(RestHighLevelClient client) {
        this.client = client;
    }

    @Override
    public CrawSearchResult search(CrawSearchParam param) {
        //要爬取的网站
        SearchRequest searchReq = buildSearchRequest(param);
        CrawSearchResult searchResult;
        try {
            //2，执行
            SearchResponse response = client.search(searchReq, RequestOptions.DEFAULT);
            searchResult = buildSearchResult(response, param);
            System.out.println(searchResult);

            //3，分析相应数据，封装成特定格式
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return searchResult;
    }

    public SearchRequest buildSearchRequest(CrawSearchParam param){
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        /*
        1，模糊匹配，过滤
        2，排序，分页
         */
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        /* 1，keyword查询 */
        if(!StringUtils.isEmpty(param.getKeyword())){
            /* 1-1，构建must条件-模糊匹配 */
            boolQuery.must(QueryBuilders.matchQuery("skuName", param.getKeyword()));
        }

        /* 2，价格区间查询 */
        if(!StringUtils.isEmpty(param.getPrice())){
            /* 1-2-5，价格区间查询 */
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");

            /* 解析价格区间查询条件 */
            String[] s = param.getPrice().split("_");
            if(s.length == 2){
                /* 区间查询 */
                rangeQuery.gte(s[0]).lte(s[1]);
            }else if(s.length == 1){
                /* 单侧查询 */
                if(param.getPrice().startsWith("_")){
                    rangeQuery.lte(s[0]);
                }else{
                    rangeQuery.gte(s[0]);
                }
            }
            boolQuery.filter(rangeQuery);
        }
        sourceBuilder.query(boolQuery);

        /* 排序、分页 */
        if(!StringUtils.isEmpty(param.getSort())){
            /* 2-1，排序*/
            String sort = param.getSort();
            String[] s = sort.split("_");
            SortOrder order = s[1].equals("asc") ? SortOrder.ASC : SortOrder.DESC;
            sourceBuilder.sort(s[0], order);
        }

        /* 2-2，分页 */
        if(param.getPage() == null || param.getPage() <= 0){
            param.setPage(1);
        }
        if(param.getSize() == null || param.getSize() <= 0){
            param.setSize(5);
        }
        sourceBuilder.from((param.getPage() - 1) * param.getSize());
        sourceBuilder.size(param.getSize());
        String dsl = sourceBuilder.toString();
        System.out.println("构建DSL:" + dsl);
        return new SearchRequest(new String[]{"e_commerce_crawler_result"}, sourceBuilder);
    }

    public CrawSearchResult buildSearchResult(SearchResponse response, CrawSearchParam param){
        CrawSearchResult searchResult = new CrawSearchResult();
        //1，返回的所有查询到的商品数据
        SearchHits hits = response.getHits();
        List<SkuCrawDoc> skuCrawDocs = new ArrayList<>();
        if(hits != null && hits.getHits().length > 0){
            for (SearchHit hit : hits) {
                String hitSourceStr = hit.getSourceAsString();
                SkuCrawDoc skuCrawDoc = JSON.parseObject(hitSourceStr, SkuCrawDoc.class);
                skuCrawDocs.add(skuCrawDoc);
            }
        }
        searchResult.setProducts(skuCrawDocs);

        //2，返回页码信息
        //分页信息-页码
        searchResult.setPage(param.getPage());
        //总记录数
        assert hits != null;
        long total = hits.getTotalHits().value;
        searchResult.setTotal(total);
        //总页码-计算得到
        int totalPages = (int) total % param.getSize() == 0?
                ((int)total / param.getSize()) :
                ((int)total / param.getSize() + 1);
        searchResult.setTotalPage(totalPages);

        return searchResult;
    }
}
