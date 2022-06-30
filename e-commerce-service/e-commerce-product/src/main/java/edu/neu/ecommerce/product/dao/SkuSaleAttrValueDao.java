package edu.neu.ecommerce.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.neu.ecommerce.product.entity.SkuSaleAttrValueEntity;
import edu.neu.ecommerce.product.vo.SkuItemSaleAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 11:48:46
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    /**
     * <h2>查询对应商品的销售属性</h2>
     * @param spuId spu的ID
     * @return 所有销售属性
     */
    List<SkuItemSaleAttrVo> getSaleAttrsBySpuId(@Param("spuId") Long spuId);

    /**
     * 根据skuId查询销售属性值
     * @param skuId
     * @return attrName:attrValue
     */
    List<String> getSkuSaleAttrValuesAsStringList(@Param("skuId") Long skuId);

}
