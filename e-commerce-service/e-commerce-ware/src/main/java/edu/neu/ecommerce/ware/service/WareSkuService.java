package edu.neu.ecommerce.ware.service;


import com.baomidou.mybatisplus.extension.service.IService;
import edu.neu.ecommerce.utils.PageUtils;
import edu.neu.ecommerce.ware.entity.WareSkuEntity;
import edu.neu.ecommerce.ware.vo.SkuHasStockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 13:22:06
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 采购成功，库存需求sku入库
     * @param skuId  商品ID
     * @param wareId 仓库ID
     * @param skuNum 商品数量
     */
    void addStock(Long skuId, Long wareId, Integer skuNum);

    /**
     * <h2>检查SKU是否还有库存</h2>
     * @return 检查结果
     */
    List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds);
}

