package edu.neu.ecommerce.product.service;


import com.baomidou.mybatisplus.extension.service.IService;
import edu.neu.ecommerce.product.entity.AttrEntity;
import edu.neu.ecommerce.product.vo.AttrGroupRelationVo;
import edu.neu.ecommerce.product.vo.AttrRespVo;
import edu.neu.ecommerce.product.vo.AttrVo;
import edu.neu.ecommerce.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 11:48:46
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    /**
     * <h2>查询信息</h2>
     *
     * @param params    查询参数
     * @param catelogId 分类ID
     * @param type 参数类型
     * @return 查询结果
     */
    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    List<AttrEntity> getRelationAttr(Long attrgroupId);

    void deleteRelation(AttrGroupRelationVo[] vos);

    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

    /**
     * <h2>查询检索属性</h2>
     * 在指定的属性集合中挑出检索属性
     * @param attrIds 指定的属性集合
     * @return 检索属性IDS
     */
    List<Long> selectSearchAttrIds(List<Long> attrIds);
}

