package edu.neu.ecommerce.product.service;


import com.baomidou.mybatisplus.extension.service.IService;
import edu.neu.ecommerce.product.entity.SkuInfoEntity;
import edu.neu.ecommerce.product.vo.SkuItemVo;
import edu.neu.ecommerce.utils.PageUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 11:48:46
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    /**
     * <h2>根据spuId查询所有Sku信息</h2>
     * @param spuId 商品集合ID
     * @return 查询结果
     */
    List<SkuInfoEntity> getSkusBySpuId(Long spuId);

    /**
     * <h2>查询指定ID的Sku信息</h2>
     * @param skuId SkuId
     * @return 返回的Sku详情信息
     */
    SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException;
}

