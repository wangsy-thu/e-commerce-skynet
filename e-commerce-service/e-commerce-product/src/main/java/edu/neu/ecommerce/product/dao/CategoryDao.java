package edu.neu.ecommerce.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.neu.ecommerce.product.entity.CategoryEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 11:48:46
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
