package edu.neu.ecommerce.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.neu.ecommerce.coupon.dao.SeckillSessionDao;
import edu.neu.ecommerce.coupon.entity.SeckillSessionEntity;
import edu.neu.ecommerce.coupon.entity.SeckillSkuRelationEntity;
import edu.neu.ecommerce.coupon.service.SeckillSessionService;
import edu.neu.ecommerce.utils.DateUtils;
import edu.neu.ecommerce.utils.PageUtils;
import edu.neu.ecommerce.utils.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    private final SeckillSkuRelationServiceImpl seckillSkuRelationService;

    public SeckillSessionServiceImpl(SeckillSkuRelationServiceImpl seckillSkuRelationService) {
        this.seckillSkuRelationService = seckillSkuRelationService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询最近三天需要秒杀的场次 和 商品
     */
    @Override
    public List<SeckillSessionEntity> getLates3DaySession() {
        // 计算最近三天起止时间
        String startTime = DateUtils.currentStartTime();// 当天00:00:00
        String endTime = DateUtils.getTimeByOfferset(2);// 后天23:59:59

        // 查询起止时间内的秒杀场次
        List<SeckillSessionEntity> sessions = baseMapper.selectList(new QueryWrapper<SeckillSessionEntity>()
                .between("start_time", startTime, endTime));

        // 组合秒杀关联的商品信息
        if (!CollectionUtils.isEmpty(sessions)) {
            // 组合场次ID
            List<Long> sessionIds = sessions.stream().map(SeckillSessionEntity::getId).collect(Collectors.toList());
            // 查询秒杀场次关联商品信息
            Map<Long, List<SeckillSkuRelationEntity>> skuMap = seckillSkuRelationService
                    .list(new QueryWrapper<SeckillSkuRelationEntity>().in("promotion_session_id", sessionIds))
                    .stream().collect(Collectors.groupingBy(SeckillSkuRelationEntity::getPromotionSessionId));
            sessions.forEach(session -> session.setRelationSkus(skuMap.get(session.getId())));
        }
        return sessions;

    }



}