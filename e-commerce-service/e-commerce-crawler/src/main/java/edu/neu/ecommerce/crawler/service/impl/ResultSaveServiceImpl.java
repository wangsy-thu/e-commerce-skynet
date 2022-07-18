package edu.neu.ecommerce.crawler.service.impl;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import edu.neu.ecommerce.crawler.document.SkuCrawDoc;
import edu.neu.ecommerce.crawler.service.ResultSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ResultSaveServiceImpl implements ResultSaveService {

    private final RestHighLevelClient client;

    public ResultSaveServiceImpl(RestHighLevelClient client) {
        this.client = client;
    }

    @Override
    public void crawSkuFromWeb(String keyword){
        String url = "https://search.dangdang.com/?key=" +
                keyword +
                "&show=list&act=input";
        //获得一个和网站的链接，注意是Jsoup的connect
        Connection connect = Jsoup.connect(url);
        //获得该网站的Document对象
        Document document;
        try {
            document = connect.get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Elements rootElement = document.select("#search_nature_rg ul li");

        List<SkuCrawDoc> crawResult = new ArrayList<>();
        for(Element ele : rootElement){
            //获取商品
            Elements skuNameElement= ele.select(".name a");
            String skuName = skuNameElement.attr("title");

            Elements skuPriceElement = ele.select(".price");
            String skuPriceStr = skuPriceElement.select(".search_now_price").text();
            BigDecimal skuPrice;
            if(!"".equals(skuPriceStr)){
                skuPriceStr = skuPriceStr.substring(1);
                skuPrice = BigDecimal.valueOf(Double.parseDouble(skuPriceStr));
            }else{
                skuPrice = new BigDecimal(0);
            }

            Elements skuImgUrlElement = ele.select(".pic img");
            String skuImgUrl = "https:" + skuImgUrlElement.attr("data-original");

            Elements shopNameElement = ele.select(".link a");
            String shopName = shopNameElement.text();
            if("".equals(shopName)){
                shopName = "自营店铺";
            }

            Elements commentNumElement = ele.select(".search_star_line a");
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
        }
        log.info("craw from web:[{}]", JSON.toJSONString(crawResult));

        boolean isSuccess;
        try {
            isSuccess = saveToES(crawResult);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("save to es success:[{}]", isSuccess);
    }

    public boolean saveToES(List<SkuCrawDoc> skuCrawDocList) throws IOException {
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
        return !b;
    }
}
