package edu.neu.ecommerce.search.service;


import edu.neu.ecommerce.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

public interface ProductSaveService {

    /**
     * <h2>保存上架商品</h2>
     * @param skuEsModels 保存的商品信息
     */
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;

    /**
     * <h2>商品下架函数</h2>
     * @param skuId 商品下架ID
     */
    void productStatusDown(Long skuId);
}
