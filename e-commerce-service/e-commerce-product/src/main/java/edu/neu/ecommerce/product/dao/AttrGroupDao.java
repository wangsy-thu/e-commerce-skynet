package edu.neu.ecommerce.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.neu.ecommerce.product.entity.AttrGroupEntity;
import edu.neu.ecommerce.product.vo.SpuItemAttrGroupVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 11:48:46
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    /**
     * <h2>查询商品的分组信息</h2>
     * @param spuId 商品ID
     * @param catalogId 三级分类ID
     * @return 属性信息
     */
    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(
            @Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}
