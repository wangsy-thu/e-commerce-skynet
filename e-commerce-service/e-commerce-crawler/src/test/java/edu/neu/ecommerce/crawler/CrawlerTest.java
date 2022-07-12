package edu.neu.ecommerce.crawler;

import com.alibaba.fastjson.JSON;
import edu.neu.ecommerce.crawler.document.SkuCrawDoc;
import edu.neu.ecommerce.crawler.vo.CrawSearchParam;
import edu.neu.ecommerce.crawler.vo.CrawSearchResult;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CrawlerTest {

    public static void main(String[] args) throws IOException {
        //要爬取的网站
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("123.56.120.188", 9200, "http")
                )
        );
        CrawSearchParam crawSearchParam = new CrawSearchParam(
                "华为手机",
                "skuPrice_desc",
                "0_9999",
                1,
                5
        );
        SearchRequest searchReq = buildSearchRequest(crawSearchParam);
        try {
            //2，执行
            SearchResponse response = client.search(searchReq, RequestOptions.DEFAULT);
            CrawSearchResult searchResult = buildSearchResult(response, crawSearchParam);
            System.out.println(searchResult);

            //3，分析相应数据，封装成特定格式
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<SkuCrawDoc> crawDataFromWeb() throws IOException {
        String url = "https://search.dangdang.com/?key=华为手机&act=input";
        //获得一个和网站的链接，注意是Jsoup的connect
        Connection connect = Jsoup.connect(url);
        //获得该网站的Document对象
        Document document = connect.get();
        Elements rootElement = document.select("#search_nature_rg ul li");

        List<SkuCrawDoc> crawResult = new ArrayList<>();
        for(Element ele : rootElement){
            //获取商品
            Elements skuNameElement= ele.select(".name a");
            String skuName = skuNameElement.attr("title");

            Elements skuPriceElement = ele.select(".price_n");
            String skuPriceStr = skuPriceElement.text();
            skuPriceStr = skuPriceStr.substring(1);
            BigDecimal skuPrice = BigDecimal.valueOf(Double.parseDouble(skuPriceStr));

            Elements skuImgUrlElement = ele.select(".pic img");
            String skuImgUrl = "https:" + skuImgUrlElement.attr("src");

            Elements shopNameElement = ele.select(".link a");
            String shopName = shopNameElement.text();
            if("".equals(shopName)){
                shopName = "自营店铺";
            }

            Elements commentNumElement = ele.select(".star a");
            String commentNumStr = commentNumElement.text();
            int commentNum = 0;
            if(!"".equals(commentNumStr)){
                commentNumStr = commentNumStr.substring(0, commentNumStr.length() - 3);
                commentNum = Integer.parseInt(commentNumStr);
            }

            SkuCrawDoc skuCrawDoc = new SkuCrawDoc(
                    null,
                    "当当网",
                    skuName,
                    skuPrice,
                    new Date(),
                    shopName,
                    skuImgUrl,
                    commentNum
            );
            crawResult.add(skuCrawDoc);

            System.out.println(skuCrawDoc);
        }
        return crawResult;
    }

    public static void saveToES(List<SkuCrawDoc> skuCrawDocList) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("123.56.120.188", 9200, "http")
                )
        );
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuCrawDoc skuCrawDoc : skuCrawDocList) {
            IndexRequest indexRequest = new IndexRequest("e_commerce_crawler_result");
            indexRequest.source(JSON.toJSONString(skuCrawDoc), XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        boolean b = bulk.hasFailures();
        List<String> co = Arrays.stream(bulk.getItems()).map(BulkItemResponse::getId)
                .collect(Collectors.toList());
        System.out.println("存储完成" + co + "是否有失败：" + b);
    }

    public static List<SkuCrawDoc> searchSku(){
        return null;
    }

    public static SearchRequest buildSearchRequest(CrawSearchParam param){
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

    public static CrawSearchResult buildSearchResult(SearchResponse response, CrawSearchParam param){
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
