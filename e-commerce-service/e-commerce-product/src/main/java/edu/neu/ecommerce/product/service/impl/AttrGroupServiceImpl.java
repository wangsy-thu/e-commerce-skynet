package edu.neu.ecommerce.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.neu.ecommerce.product.dao.AttrGroupDao;
import edu.neu.ecommerce.product.entity.AttrEntity;
import edu.neu.ecommerce.product.entity.AttrGroupEntity;
import edu.neu.ecommerce.product.service.AttrGroupService;
import edu.neu.ecommerce.product.service.AttrService;
import edu.neu.ecommerce.product.vo.AttrGroupWithAttrsVo;
import edu.neu.ecommerce.product.vo.SpuItemAttrGroupVo;
import edu.neu.ecommerce.utils.PageUtils;
import edu.neu.ecommerce.utils.Query;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {


    @Autowired
    private AttrService attrService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catlogId) {

        String key = ((String) params.get("key"));
        // select * from pms_attr_group where catlogId = ?
        QueryWrapper<AttrGroupEntity> wrapper
                = new QueryWrapper<>();
        if(!StringUtils.isEmpty(key)){
            wrapper.and((obj) -> obj.eq("attr_group_id", key)
                    .or().like("attr_group_name", key));
        }
        if(catlogId == 0){
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );
            return new PageUtils(page);
        } else {
            wrapper.eq("catelog_id", catlogId);
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );
            return new PageUtils(page);
        }
    }

    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        //查询分组信息
        List<AttrGroupEntity> attrGroupEntities
                = this.list(new QueryWrapper<AttrGroupEntity>()
                .eq("catelog_id", catelogId));
        return attrGroupEntities.stream().map(group -> {
            AttrGroupWithAttrsVo attrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(group, attrsVo);
            List<AttrEntity> attrs = attrService.getRelationAttr(attrsVo.getAttrGroupId());
            attrsVo.setAttrs(attrs);
            return attrsVo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
        //1，查出当前Spu对应的所有属性的分组信息以及当前分组下的所有属性对应的值
        //1-1，查出所有属性分组
        /*
        SELECT pav.`spu_id`, ag.`attr_group_name`, ag.`attr_group_id`, aar.`attr_id`, attr.`attr_name`, pav.`attr_value`
        FROM `pms_attr_group` ag
        LEFT JOIN `pms_attr_attrgroup_relation` aar
        ON aar.`attr_group_id` = ag.`attr_group_id`
        LEFT JOIN `pms_attr` attr
        ON attr.`attr_id` = aar.`attr_id`
        LEFT JOIN `pms_product_attr_value` pav
        ON pav.`attr_id` = attr.`attr_id`
        WHERE ag.catelog_id = 225 AND pav.`spu_id` = 22
         */
        return baseMapper.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
    }
}