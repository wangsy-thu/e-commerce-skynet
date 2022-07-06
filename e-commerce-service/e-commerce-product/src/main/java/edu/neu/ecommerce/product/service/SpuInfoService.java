package edu.neu.ecommerce.product.service;


import com.baomidou.mybatisplus.extension.service.IService;
import edu.neu.ecommerce.product.entity.SpuInfoEntity;
import edu.neu.ecommerce.product.vo.SpuSaveVo;
import edu.neu.ecommerce.utils.PageUtils;

import java.util.Map;

/**
 * spu信息
 *
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 11:48:46
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * <h2>保存SPU信息</h2>
     * @param vo spu值对象
     */
    void saveSpuInfo(SpuSaveVo vo);

    void saveBaseSpuInfo(SpuInfoEntity infoEntity);

    PageUtils queryPagByCondition(Map<String, Object> params);

    /**
     * <h2>商品上架</h2>
     * @param spuId 上架商品的SPU ID
     */
    void up(Long spuId);

    SpuInfoEntity getSpuInfoBySkuId(Long skuId);
}

