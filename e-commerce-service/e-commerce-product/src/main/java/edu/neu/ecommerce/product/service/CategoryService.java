package edu.neu.ecommerce.product.service;


import com.baomidou.mybatisplus.extension.service.IService;
import edu.neu.ecommerce.product.entity.CategoryEntity;
import edu.neu.ecommerce.product.vo.Catelog2Vo;
import edu.neu.ecommerce.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 11:48:46
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * <h2>返回树形结构的分类</h2>
     * @return 分类数据
     */
    List<CategoryEntity> listWithTree();

    /**
     * 批量删除分类信息
     * @param asList 分类ID
     */
    void removeMenuByIds(List<Long> asList);

    /**
     * 找到三级分类的路径
     * @param attrGroupId 三级分类ID
     * @return 三级分类路径
     */
    Long[] findCatelogPath(Long attrGroupId);

    /**
     * <h2>级联更新</h2>
     * @param category 分类信息
     */
    void updateCascade(CategoryEntity category);

    /**
     * <h2>查询所有的一级分类</h2>
     * @return 所有一级分类信息
     */
    List<CategoryEntity> getLevel1Categorys();

    /**
     * <h2>查出所有分类数据</h2>
     * @return 分类的Map数据
     */
    Map<String, List<Catelog2Vo>> getCatelogJson();
}

