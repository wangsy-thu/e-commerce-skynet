package edu.neu.ecommerce.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import edu.neu.ecommerce.search.config.GulimallElasticSearchConfig;
import edu.neu.ecommerce.search.constant.EsConstant;
import edu.neu.ecommerce.search.feign.ProductFeignService;
import edu.neu.ecommerce.search.service.MallSearchService;
import edu.neu.ecommerce.search.vo.AttrResponseVo;
import edu.neu.ecommerce.search.vo.SearchParam;
import edu.neu.ecommerce.search.vo.SearchResult;
import edu.neu.ecommerce.to.es.SkuEsModel;
import edu.neu.ecommerce.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MallSearchServiceImpl implements MallSearchService {

    private final RestHighLevelClient client;

    private final ProductFeignService productFeignService;

    public MallSearchServiceImpl(RestHighLevelClient client,
                                 ProductFeignService productFeignService) {
        this.client = client;
        this.productFeignService = productFeignService;
    }

    @Override
    public SearchResult search(SearchParam param) {
        //1，动态构建出查询DSL
        SearchResult result;

        //1，准备检索请求
        SearchRequest searchRequest = buildSearchRequest(param);
        try {
            //2，执行
            SearchResponse response = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);

            result = buildSearchResult(response, param);

            //3，分析相应数据，封装成特定格式
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * <h2>构建结果数据</h2>
     * @return 相应结果
     */
    private SearchResult buildSearchResult(SearchResponse response, SearchParam param) {
        SearchResult result = new SearchResult();
        //1，返回的所有查询到的商品数据
        SearchHits hits = response.getHits();
        List<SkuEsModel> esModels = new ArrayList<>();
        if(hits != null && hits.getHits().length > 0){
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                if(!StringUtils.isEmpty(param.getKeyword())){
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String string = skuTitle.getFragments()[0].string();
                    esModel.setSkuTitle(string);
                }
                esModels.add(esModel);
            }
        }
        result.setProducts(esModels);

        //2，返回当前所有商品涉及到的属性集
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attr_agg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            /* 属性的ID */
            long attrId = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attrId);
            /* 属性的名字 */
            ParsedStringTerms attr_name_agg = bucket.getAggregations().get("attr_name_agg");
            String attrName = attr_name_agg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);
            /* 属性的所有值 */
            ParsedStringTerms attr_value_agg = bucket.getAggregations().get("attr_value_agg");
            List<String> attrValues = attr_value_agg.getBuckets().stream()
                    .map(MultiBucketsAggregation.Bucket::getKeyAsString)
                    .collect(Collectors.toList());
            attrVo.setAttrValue(attrValues);
            attrVos.add(attrVo);
            result.getAttrIds().add(attrId);
        }
        result.setAttrs(attrVos);

        //3，返回当前所有商品涉及到的品牌信息
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            //1，品牌的ID
            long brandId = bucket.getKeyAsNumber().longValue();
            //2，品牌的图片
            ParsedStringTerms brand_img_agg = bucket.getAggregations().get("brand_img_agg");
            String brandImg = brand_img_agg.getBuckets().get(0).getKeyAsString();

            //3，品牌的名字
            ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
            String brandName = brand_name_agg.getBuckets().get(0).getKeyAsString();

            /* 封装品牌信息 */
            brandVo.setBrandId(brandId);
            brandVo.setBrandName(brandName);
            brandVo.setBrandImg(brandImg);
            brandVos.add(brandVo);
        }
        result.setBrands(brandVos);

        //4，涉及到的分类信息
        ParsedLongTerms catalog_agg = response.getAggregations().get("catalog_agg");
        List<? extends Terms.Bucket> buckets = catalog_agg.getBuckets();
        List<SearchResult.CatalogVo>  catalogVos = new ArrayList<>();
        for (Terms.Bucket bucket : buckets) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            /* 得到分类ID */
            String keyAsString = bucket.getKeyAsString();
            catalogVo.setCatalogId(Long.parseLong(keyAsString));
            ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
            String catalog_name = catalog_name_agg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalog_name);
            catalogVos.add(catalogVo);
        }
        result.setCatalogs(catalogVos);
        //5，涉及到的分页信息
        //分页信息-页码
        result.setPageNum(param.getPageNum());
        //总记录数
        assert hits != null;
        long total = hits.getTotalHits().value;
        result.setTotal(total);
        //总页码-计算得到
        int totalPages = (int) total % EsConstant.PRODUCT_PAGESIZE == 0?
                ((int)total / EsConstant.PRODUCT_PAGESIZE) :
                ((int)total / EsConstant.PRODUCT_PAGESIZE + 1);
        result.setTotalPages(totalPages);

        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);

        /* 6，构建面包屑导航功能 */
        if(param.getAttrs() != null && param.getAttrs().size() > 0){
            List<SearchResult.NavVo> navVos = param.getAttrs().stream().map(attr -> {
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                R r = productFeignService.attrInfo(Long.parseLong(s[0]));
                result.getAttrIds().add(Long.parseLong(s[0]));
                if(r.getCode() == 0){
                    AttrResponseVo data = r.getData("attr", new TypeReference<AttrResponseVo>() {
                    });
                    log.info("get data:[{}]", JSON.toJSONString(data));
                    log.info("current nav:[{}]", JSON.toJSONString(navVo));
                    navVo.setNavName(data.getAttrName());
                }else {
                    navVo.setNavName(s[0]);
                }
                //拿到所有的查询条件，去掉当前
                String replace = replaceQueryString(param, attr, "attrs");
                navVo.setLink("http://search.gulimall.com/list.html?" + replace);
                return navVo;
            }).collect(Collectors.toList());
            result.setNavs(navVos);
        }

        /* 品牌面包屑返回 */
        /*if(param.getBrandId() != null && param.getBrandId().size() > 0){
            List<SearchResult.NavVo> navs = result.getNavs();
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            navVo.setNavName("品牌");
            //远程调用品牌
            R r = productFeignService.brandsInfo(param.getBrandId());
            if(r.getCode() == 0){
                List<BrandVo> brand = r.getData("brand", new TypeReference<List<BrandVo>>() {
                });
                log.info("brand:[{}]", JSON.toJSONString(brand));
                StringBuilder buffer = new StringBuilder();
                String replace = "";
                for (BrandVo brandVo : brand) {
                    buffer.append(brandVo.getBrandName()).append(";");
                    log.info("brandName:[{}]", JSON.toJSONString(brandVo.getBrandName()));
                    replace = replaceQueryString(param, brandVo.getBrandName() ,"brandId");
                }
                navVo.setNavValue(buffer.toString());
                navVo.setLink("http://search.gulimall.com/list.html?" + replace);
            }
            navs.add(navVo);
        }*/
        return result;
    }

    private String replaceQueryString(SearchParam param, String attr, String key) {
        String encode = null;
        try {
            encode = URLEncoder.encode(attr, "UTF-8");
            encode = encode.replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return param.get_queryString().replace("&" + key + "=" + encode, "");
    }

    /**
     * <h2>创建检索请求</h2>
     * @return 检索请求
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        /*
        1，模糊匹配，过滤
        2，排序，分页，高亮
        3，聚合分析
         */

        /* 1，查询：模糊匹配，过滤（属性，分类，品牌，价格区间，库存） */
        /* 1，构建bool查询 */
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if(!StringUtils.isEmpty(param.getKeyword())){
            /* 1-1，构建must条件-模糊匹配 */
            boolQuery.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
        }
        /* 1-2，构建过滤条件 */
        if(param.getCatalog3Id() != null){
            /* 1-2-1，三级分类ID查询 */
            boolQuery.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
        }

        if(param.getBrandId() != null && param.getBrandId().size() > 0){
            /* 1-2-2，品牌ID查询 */
            boolQuery.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
        }

        if(param.getAttrs() != null && param.getAttrs().size() > 0){
            /* 1-2-3，属性查询 */
            for (String attrStr : param.getAttrs()) {
                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                String[] s = attrStr.split("_");
                /* 解析属性ID */
                String attrId = s[0];
                /* 解析属性值 */
                String[] attrValues = s[1].split(":");
                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                NestedQueryBuilder nestedQuery =
                        QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);
                boolQuery.filter(nestedQuery);
            }
        }

        /* 1-2-4，是否有库存查询 */
        if(param.getHasStock() != null){
            boolQuery.filter(QueryBuilders.termsQuery("hasStock", param.getHasStock() == 1));
        }

        if(!StringUtils.isEmpty(param.getSkuPrice())){
            /* 1-2-5，价格区间查询 */
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");

            /* 解析价格区间查询条件 */
            String[] s = param.getSkuPrice().split("_");
            if(s.length == 2){
                /* 区间查询 */
                rangeQuery.gte(s[0]).lte(s[1]);
            }else if(s.length == 1){
                /* 单侧查询 */
                if(param.getSkuPrice().startsWith("_")){
                    rangeQuery.lte(s[0]);
                }else{
                    rangeQuery.gte(s[0]);
                }
            }
            boolQuery.filter(rangeQuery);
        }

        /* 整合所有查询条件 */
        sourceBuilder.query(boolQuery);

        /* 2，排序，分页，高亮 */
        if(!StringUtils.isEmpty(param.getSort())){
            /* 2-1，排序*/
            String sort = param.getSort();
            String[] s = sort.split("_");
            SortOrder order = s[1].equals("asc") ? SortOrder.ASC : SortOrder.DESC;
            sourceBuilder.sort(s[0], order);
        }

        /* 2-2，分页 */
        sourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
        sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);

        /* 2-3，高亮*/
        if(!StringUtils.isEmpty(param.getKeyword())){
            HighlightBuilder builder = new HighlightBuilder();
            builder.field("skuTitle");
            builder.preTags("<b style='color:red'>");
            builder.postTags("</b>");
            sourceBuilder.highlighter(builder);
        }

        /* 聚合分析 */
        /* 1，品牌聚合 */
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);
        /* 1-1，子聚合-名字、图片 */
        brand_agg.subAggregation(AggregationBuilders
                .terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders
                .terms("brand_img_agg").field("brandImg").size(1));
        sourceBuilder.aggregation(brand_agg);

        /* 2，分类聚合 */
        TermsAggregationBuilder catalog_agg = AggregationBuilders
                .terms("catalog_agg").field("catalogId").size(20);
        catalog_agg.subAggregation(AggregationBuilders
                .terms("catalog_name_agg").field("catalogName").size(1));
        sourceBuilder.aggregation(catalog_agg);

        /* 3，属性聚合 */
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        /* 聚合出所有的attrId */
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        /* 聚合出所有的attrName */
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        /* 聚合出所有的attrValue能够取的值 */
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        attr_agg.subAggregation(attr_id_agg);
        sourceBuilder.aggregation(attr_agg);

        String dsl = sourceBuilder.toString();
        System.out.println("构建的DSL:" + dsl);
        return new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
    }
}
