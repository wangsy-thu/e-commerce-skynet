package edu.neu.ecommerce.coupon.service;


import com.baomidou.mybatisplus.extension.service.IService;
import edu.neu.ecommerce.coupon.entity.SkuFullReductionEntity;
import edu.neu.ecommerce.to.SkuReductionTo;
import edu.neu.ecommerce.utils.PageUtils;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 12:51:34
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(SkuReductionTo skuReductionTo);
}

