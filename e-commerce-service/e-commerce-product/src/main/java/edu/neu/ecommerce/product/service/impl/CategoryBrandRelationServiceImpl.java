package edu.neu.ecommerce.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.neu.ecommerce.product.dao.BrandDao;
import edu.neu.ecommerce.product.dao.CategoryBrandRelationDao;
import edu.neu.ecommerce.product.dao.CategoryDao;
import edu.neu.ecommerce.product.entity.BrandEntity;
import edu.neu.ecommerce.product.entity.CategoryBrandRelationEntity;
import edu.neu.ecommerce.product.entity.CategoryEntity;
import edu.neu.ecommerce.product.service.BrandService;
import edu.neu.ecommerce.product.service.CategoryBrandRelationService;
import edu.neu.ecommerce.utils.PageUtils;
import edu.neu.ecommerce.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    BrandDao brandDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    CategoryBrandRelationDao relationDao;

    @Autowired
    BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    @Transactional
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();
        BrandEntity brandEntity = brandDao.selectById(brandId);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
        this.save(categoryBrandRelation);
    }

    @Override
    public void updateBrand(Long brandId, String name) {
        CategoryBrandRelationEntity relationEntity = new CategoryBrandRelationEntity();
        relationEntity.setBrandId(brandId);
        relationEntity.setBrandName(name);
        this.update(relationEntity, new UpdateWrapper<CategoryBrandRelationEntity>()
                .eq("brand_id", brandId));
    }

    @Override
    public void updateCategory(Long catId, String name) {
        this.baseMapper.updateCategory(catId, name);
    }

    /**
     * 根据三级分类ID查询所有品牌
     * 但是封装了品牌的所有信息，为了该service方法重用
     */
    @Override
    public List<BrandEntity> getBrandsByCatId(Long catId) {
        // 查询分类关联的所有品牌
        List<CategoryBrandRelationEntity> relationEntities = relationDao.
                selectList(new QueryWrapper<CategoryBrandRelationEntity>()
                        .eq("catelog_id", catId));
        if (!CollectionUtils.isEmpty(relationEntities)) {
            // 封装品牌IDs
            List<Long> brandIds = relationEntities.stream().map(CategoryBrandRelationEntity::getBrandId).collect(Collectors.toList());
            return brandDao.selectBatchIds(brandIds);
        }
        return new ArrayList<>();
    }
}