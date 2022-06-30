package edu.neu.ecommerce.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.neu.ecommerce.product.entity.ProductAttrValueEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * spu属性值
 * 
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 11:48:46
 */
@Mapper
public interface ProductAttrValueDao extends BaseMapper<ProductAttrValueEntity> {
	
}
