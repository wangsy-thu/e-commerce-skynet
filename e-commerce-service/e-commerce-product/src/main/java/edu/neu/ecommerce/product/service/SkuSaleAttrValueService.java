package edu.neu.ecommerce.product.service;


import com.baomidou.mybatisplus.extension.service.IService;
import edu.neu.ecommerce.product.entity.SkuSaleAttrValueEntity;
import edu.neu.ecommerce.product.vo.SkuItemSaleAttrVo;
import edu.neu.ecommerce.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 11:48:46
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * <h2>查找所有的销售属性</h2>
     * @param spuId SPU ID
     * @return 销售属性
     */
    List<SkuItemSaleAttrVo> getSaleAttrsBySpuId(Long spuId);

    /**
     * 根据skuId查询销售属性值
     * @param skuId
     * @return skuId:skuValue
     */
    List<String> getSkuSaleAttrValuesAsStringList(Long skuId);
}

