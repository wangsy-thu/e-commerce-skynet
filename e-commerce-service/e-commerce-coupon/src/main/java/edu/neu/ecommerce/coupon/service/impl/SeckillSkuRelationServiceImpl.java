package edu.neu.ecommerce.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.neu.ecommerce.coupon.dao.SeckillSkuRelationDao;
import edu.neu.ecommerce.coupon.entity.SeckillSkuRelationEntity;
import edu.neu.ecommerce.coupon.service.SeckillSkuRelationService;
import edu.neu.ecommerce.utils.PageUtils;
import edu.neu.ecommerce.utils.Query;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity> implements SeckillSkuRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SeckillSkuRelationEntity> wrapper = new QueryWrapper<>();
        String promotionSessionId = (String) params.get("promotionSessionId");
        if (StringUtils.isNotBlank(promotionSessionId)) {
            wrapper.eq("promotion_session_id", promotionSessionId);
        }
        IPage<SeckillSkuRelationEntity> page = this.page(
                new Query<SeckillSkuRelationEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 修改redis中的上架的商品数据
     */
    @Override
    public void updateSeckillSkuRedis(SeckillSkuRelationEntity seckillSkuRelation) {

    }

    /**
     * 删除对应的redis中上架的商品数据
     */
    @Override
    public void deleteSeckillSkuRedis(List<Long> ids) {

    }
}