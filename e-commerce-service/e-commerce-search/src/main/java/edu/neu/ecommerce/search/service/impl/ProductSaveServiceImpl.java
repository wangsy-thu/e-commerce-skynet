package edu.neu.ecommerce.search.service.impl;

import com.alibaba.fastjson.JSON;
import edu.neu.ecommerce.search.config.GulimallElasticSearchConfig;
import edu.neu.ecommerce.search.constant.EsConstant;
import edu.neu.ecommerce.search.service.ProductSaveService;
import edu.neu.ecommerce.to.es.SkuEsModel;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        //1，es中建立索引，建立好映射关系
        //2，es中保存数据
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel model : skuEsModels) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(model.getSkuId().toString());
            indexRequest.source(JSON.toJSONString(model), XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = client.bulk(bulkRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
        //TODO 如果批量错误，处理
        boolean b = bulk.hasFailures();
        List<String> co = Arrays.stream(bulk.getItems()).map(BulkItemResponse::getId)
                .collect(Collectors.toList());
        log.info("商品上架完成，{}，返回数据:{}",co, bulk);
        return b;
    }
}
