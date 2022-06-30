package edu.neu.ecommerce.product.service;


import com.baomidou.mybatisplus.extension.service.IService;
import edu.neu.ecommerce.product.entity.BrandEntity;
import edu.neu.ecommerce.product.entity.CategoryBrandRelationEntity;
import edu.neu.ecommerce.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 11:48:46
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    void updateBrand(Long brandId, String name);

    void updateCategory(Long catId, String name);

    List<BrandEntity> getBrandsByCatId(Long catId);
}

