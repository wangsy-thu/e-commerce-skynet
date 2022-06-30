package edu.neu.ecommerce.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.neu.ecommerce.product.entity.AttrGroupEntity;
import edu.neu.ecommerce.product.vo.AttrGroupWithAttrsVo;
import edu.neu.ecommerce.product.vo.SpuItemAttrGroupVo;
import edu.neu.ecommerce.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 11:48:46
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * <h2>分页查询</h2>
     * @param params 查询参数
     * @param catlogId 三级分类
     * @return 分页查询结果
     */
    PageUtils queryPage(Map<String, Object> params, Long catlogId);

    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId);

    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId);
}

