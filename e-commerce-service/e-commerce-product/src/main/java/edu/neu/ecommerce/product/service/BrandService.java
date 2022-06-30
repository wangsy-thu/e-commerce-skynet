package edu.neu.ecommerce.product.service;


import com.baomidou.mybatisplus.extension.service.IService;
import edu.neu.ecommerce.product.entity.BrandEntity;
import edu.neu.ecommerce.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 11:48:46
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 冗余字段更新
     * @param brand 新的brand信息
     */
    void updateDetail(BrandEntity brand);

    List<BrandEntity> getBrandsByIds(List<Long> brandId);
}

