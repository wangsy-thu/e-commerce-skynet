package edu.neu.ecommerce.seckill.service.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import edu.neu.ecommerce.constant.SeckillConstant;
import edu.neu.ecommerce.seckill.feign.CouponFeignService;
import edu.neu.ecommerce.seckill.feign.ProductFeignService;
import edu.neu.ecommerce.seckill.interceptor.LoginUserInterceptor;
import edu.neu.ecommerce.seckill.service.SeckillService;
import edu.neu.ecommerce.seckill.to.SeckillSkuRedisTo;
import edu.neu.ecommerce.seckill.vo.SeckillSessionWithSkus;
import edu.neu.ecommerce.seckill.vo.SeckillSkuVo;
import edu.neu.ecommerce.seckill.vo.SkuInfoVo;
import edu.neu.ecommerce.to.mq.SeckillOrderTo;
import edu.neu.ecommerce.utils.R;
import edu.neu.ecommerce.vo.MemberResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    RedissonClient redissonClient;

    /**
     * 上架最近三天需要秒杀的商品
     */
    @Override
    public void uploadSeckillSkuLatest3Days() {
        // 1.查询最近三天需要参加秒杀的场次+商品
        R lates3DaySession = couponFeignService.getLates3DaySession();
        if (lates3DaySession.getCode() == 0) {
            // 获取场次
            List<SeckillSessionWithSkus> sessions = lates3DaySession.getData("data", new TypeReference<List<SeckillSessionWithSkus>>() {
            });
//            System.err.println(sessions.get(1));
            // 缓存到redis
            // 2. 缓存活动信息 上架场次信息
//            System.err.println("saveSessionInfos ==> 1  ");
            saveSessionInfos(sessions);
            // 3. 缓存活动的关联商品的信息 上架商品信息
//            System.err.println("saveSessionSkuInfo ==> 2  ");
            saveSessionSkuInfo(sessions);
        }
    }

    /**
     * 缓存活动信息 上架场次
     */
    private void saveSessionInfos(List<SeckillSessionWithSkus> sessions) {
        if (!CollectionUtils.isEmpty(sessions)) {
            sessions.stream().forEach(session -> {
                // 1.遍历场次
                long startTime = session.getStartTime().getTime();// 场次开始时间戳
                long endTime = session.getEndTime().getTime();// 场次结束时间戳
                String key = SeckillConstant.SESSION_CACHE_PREFIX + startTime + "_" + endTime;// 场次的key

                // 2.判断场次是否已上架（幂等性）
                Boolean hasKey = redisTemplate.hasKey(key);
//                System.err.println("1,1  redisTemplate.hasKey(key)==>   "+hasKey);
                if (!hasKey) {
                    // 未上架
//                    System.err.println("1,2  session.getRelationSkus()==>   "+session.getRelationSkus());
                    // 3.封装场次信息（如果场次中没有关联商品，则直接为null）
                    if (session.getRelationSkus() != null) {
//                        System.err.println("1,3  session.getRelationSkus()==>   "+session.getRelationSkus());
                        List<String> skuIds = session.getRelationSkus().stream()
                                .map(item -> item.getPromotionSessionId() + "_" + item.getSkuId().toString())
                                .collect(Collectors.toList());// skuId集合
                        // 4.上架
                        redisTemplate.opsForList().leftPushAll(key, skuIds);
                    }
                }
//                else {
                    // 已经上架，但是有商品变动（通过key，修改skuIds）[在修改变动的地方修改]
//                    if (session.getRelationSkus() != null) {
//                        System.err.println("1,3  session.getRelationSkus()==>   "+session.getRelationSkus());
//                        List<String> skuIds = session.getRelationSkus().stream()
//                                .map(item -> item.getPromotionSessionId() + "_" + item.getSkuId().toString())
//                                .collect(Collectors.toList());// skuId集合
//                        // 4.上架
//                        redisTemplate.opsForList().leftPushAll(key, skuIds);
//                    }else {
//
//                    }
//                }
            });
        }
    }

    /**
     * 上架商品信息
     */
    private void saveSessionSkuInfo(List<SeckillSessionWithSkus> sessions) {
        if (!CollectionUtils.isEmpty(sessions)) {
            // 查询所有商品信息
            List<Long> skuIds = new ArrayList<>();
            sessions.stream().forEach(session -> {
                if (session.getRelationSkus() != null) {
                    List<Long> ids = session.getRelationSkus().stream().map(SeckillSkuVo::getSkuId).collect(Collectors.toList());
                    skuIds.addAll(ids);
                }
            });
            R info = productFeignService.getSkuInfos(skuIds);
            if (info.getCode() == 0) {
                // 将查询结果封装成Map集合
                Map<Long, SkuInfoVo> skuMap = info.getData(new TypeReference<List<SkuInfoVo>>() {
                }).stream().collect(Collectors.toMap(SkuInfoVo::getSkuId, val -> val));
                // 绑定秒杀商品hash
                BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(SeckillConstant.SECKILL_CHARE_KEY);
                // 1.遍历场次
                sessions.stream().forEach(session -> {
                    // 2.遍历商品
                    if (session.getRelationSkus() != null) {
                        session.getRelationSkus().stream().forEach(seckillSku -> {
                            // 判断商品是否已上架（幂等性）
                            String skuKey = seckillSku.getPromotionSessionId().toString() + "_" + seckillSku.getSkuId().toString();// 商品的key（需要添加场次ID前缀，同一款商品可能场次不同）
                            if (!operations.hasKey(skuKey)) {
                                // 未上架
                                // 3.封装商品信息
                                SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();// 存储到redis的对象
                                SkuInfoVo sku = skuMap.get(seckillSku.getSkuId());
                                BeanUtils.copyProperties(seckillSku, redisTo);// 3.1、商品秒杀信息
                                redisTo.setSkuInfo(sku);// 3.2、商品详细信息
                                redisTo.setStartTime(session.getStartTime().getTime());// 秒杀开始时间
                                redisTo.setEndTime(session.getEndTime().getTime());// 秒杀结束时间
                                // 商品随机码：用户参与秒杀时，请求需要带上随机码（防止恶意攻击）
                                String token = UUID.randomUUID().toString().replace("-", "");// 商品随机码（随机码只会在秒杀开始时暴露）
                                redisTo.setRandomCode(token);// 设置商品随机码

                                // 4.上架商品（序列化成json格式存入Redis中）
                                String jsonString = JSONObject.toJSONString(redisTo);
                                operations.put(skuKey, jsonString);

                                // 5.上架商品的分布式信号量，key：商品随机码 值：库存（限流）
                                // 引入分布式的信号量 带着随机码减信号量
                                RSemaphore semaphore = redissonClient.getSemaphore(SeckillConstant.SKU_STOCK_SEMAPHORE + token);
                                // 信号量（扣减成功才进行后续操作，否则快速返回）商品可以秒杀的数量
                                semaphore.trySetPermits(seckillSku.getSeckillCount());
                            }
                        });
                    }
                });
            }
        }
    }

    /**
     * 获取到当前可以参加秒杀商品的信息
     */
    @SentinelResource(value = "getCurrentSeckillSkusResource", blockHandler = "blockHandler")
    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {

        // 自定义资源进行限流
        try (Entry entry = SphU.entry("seckillSkus")) {
            // 1.查询当前时间所属的秒杀场次
            long currentTime = System.currentTimeMillis();// 当前时间
            // 查询所有秒杀场次的key
            Set<String> keys = redisTemplate.keys(SeckillConstant.SESSION_CACHE_PREFIX + "*");// keys seckill:sessions:*
            for (String key : keys) {
                //seckill:sessions:1594396764000_1594453242000
                String replace = key.replace(SeckillConstant.SESSION_CACHE_PREFIX, "");// 截取时间，去掉前缀
                String[] time = replace.split("_");
                long startTime = Long.parseLong(time[0]);// 开始时间
                long endTime = Long.parseLong(time[1]);// 截止时间
                // 判断是否处于该场次
                if (currentTime >= startTime && currentTime <= endTime) {
                    // 2.查询当前场次信息（查询结果List< sessionId_skuId > ）
                    List<String> sessionIdSkuIds = redisTemplate.opsForList().range(key, -100, 100);// 获取list范围内100条数据
    //                System.err.println(sessionIdSkuIds);
                    // 获取商品信息
                    BoundHashOperations<String, String, String> skuOps = redisTemplate.boundHashOps(SeckillConstant.SECKILL_CHARE_KEY);
                    assert sessionIdSkuIds != null;
                    // 根据List< sessionId_skuId >从Map中批量获取商品信息
                    List<String> skus = skuOps.multiGet(sessionIdSkuIds);
    //                System.err.println(skus);
                    if (!CollectionUtils.isEmpty(skus)) {
                        // 将商品信息反序列成对象
                        List<SeckillSkuRedisTo> skuInfos = skus.stream().map(sku -> {
                            SeckillSkuRedisTo skuInfo = JSON.parseObject(sku.toString(), SeckillSkuRedisTo.class);
                            // redisTo.setRandomCode(null);当前秒杀开始需要随机码
                            return skuInfo;
                        }).collect(Collectors.toList());
    //                    System.err.println("当前商品秒杀信息："+skuInfos);
                        return skuInfos;
                    }
                    // 3.匹配场次成功，退出循环
                    break;
                }
            }
        } catch (BlockException e) {
            log.error("资源被限流，{}", e.getMessage());
        }
        return null;
    }

    /**
     * 降级或限流处理
     * @param e
     * @return
     */
    public List<SeckillSkuRedisTo> blockHandler(BlockException e) {
        log.error("getCurrentSeckillSkusResource被限流了,{}", e.getMessage());
        return null;
    }

    /**
     * 根据skuId查询商品当前时间秒杀信息
     *
     * @param skuId
     */
    @Override
    public SeckillSkuRedisTo getSkuSeckilInfo(Long skuId) {
        // 1.匹配查询当前商品的秒杀信息
        BoundHashOperations<String, String, String> skuOps = redisTemplate.boundHashOps(SeckillConstant.SECKILL_CHARE_KEY);
        // 获取所有商品的key：sessionId_
        Set<String> keys = skuOps.keys();
        if (!CollectionUtils.isEmpty(keys)) {
            String lastIndex = "_" + skuId;
            for (String key : keys) {
                if (key.lastIndexOf(lastIndex) > -1) {
                    // 商品id匹配成功
                    String jsonString = skuOps.get(key);
                    // 进行序列化
                    SeckillSkuRedisTo skuInfo = JSON.parseObject(jsonString, SeckillSkuRedisTo.class);
                    Long currentTime = System.currentTimeMillis();
                    Long endTime = skuInfo.getEndTime();
                    if (currentTime <= endTime) {
                        // 当前时间小于截止时间
                        Long startTime = skuInfo.getStartTime();
                        if (currentTime >= startTime) {
                            // 返回当前正处于秒杀的商品信息
                            return skuInfo;
                        }
                        // 还没有开始，返回预告信息，不返回随机码
                        skuInfo.setRandomCode(null);// 随机码
                        return skuInfo;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 秒杀商品
     * 1.校验登录状态
     * 2.校验秒杀时间
     * 3.校验随机码、场次、商品对应关系
     * 4.校验信号量扣减，校验购物数量是否限购
     * 5.校验是否重复秒杀（幂等性）【秒杀成功SETNX占位  userId_sessionId_skuId】
     * 6.扣减信号量
     * 7.发送消息，创建订单号和订单信息
     * 8.订单模块消费消息，生成订单
     * @param killId    sessionId_skuid
     * @param key   随机码
     * @param num   商品件数
     */
    @Override
    public String kill(String killId, String key, Integer num) throws InterruptedException {
        // 1.拦截器校验登录状态
        long start = System.currentTimeMillis();
        // 获取当前用户信息
        MemberResponseVo user = LoginUserInterceptor.loginUser.get();

        // 获取当前秒杀商品的详细信息
        BoundHashOperations<String, String, String> skuOps = redisTemplate.boundHashOps(SeckillConstant.SECKILL_CHARE_KEY);
        // killId 是xx_xx
        String jsonString = skuOps.get(killId);// 根据sessionId_skuid获取秒杀商品信息
        if (StringUtils.isEmpty(jsonString)) {
            // 这一步已经默认校验了场次+商品，如果为空表示校验失败
            return null;
        }
        // json反序列化商品信息
        SeckillSkuRedisTo skuInfo = JSON.parseObject(jsonString, SeckillSkuRedisTo.class);
        // 校验合法性
        Long startTime = skuInfo.getStartTime();
        Long endTime = skuInfo.getEndTime();
        long currentTime = System.currentTimeMillis();
        // 2.校验秒杀时间
        if (currentTime >= startTime && currentTime <= endTime) {
            // 3.校验随机码
            String randomCode = skuInfo.getRandomCode();// 随机码
            if (randomCode.equals(key)) {
                // 获取每人限购数量
                Integer seckillLimit = skuInfo.getSeckillLimit();
                // 获取信号量
                String seckillCount = redisTemplate.opsForValue().get(SeckillConstant.SKU_STOCK_SEMAPHORE + randomCode);
                Integer count = Integer.valueOf(seckillCount);
                // 4.校验信号量（库存是否充足）、校验购物数量是否限购
                if (num > 0 && num <= seckillLimit && count > num) {
                    // 5.校验是否重复秒杀（幂等性）【秒杀成功后占位，userId-sessionId-skuId】
                    // SETNX 原子性处理
                    String userKey = SeckillConstant.SECKILL_USER_PREFIX + user.getId() + "_" + killId;
                    // 自动过期时间(活动结束时间 - 当前时间)
                    Long ttl = endTime - currentTime;
                    Boolean isRepeat = redisTemplate.opsForValue().setIfAbsent(userKey, num.toString(), ttl, TimeUnit.MILLISECONDS);
                    if (isRepeat) {
                        // 占位成功，说明从来没有买过
                        // 6.扣减信号量（防止超卖）
                        RSemaphore semaphore = redissonClient.getSemaphore(SeckillConstant.SKU_STOCK_SEMAPHORE + randomCode);
                        boolean isAcquire = semaphore.tryAcquire(num, 100, TimeUnit.MILLISECONDS);
                        if (isAcquire) {
                            // 信号量扣减成功，秒杀成功，快速下单
                            // 7.发送消息，创建订单号和订单信息
                            // 秒杀成功 快速下单 发送消息到 MQ 整个操作时间在 10ms 左右
                            String orderSn = IdWorker.getTimeId();// 订单号
                            SeckillOrderTo order = new SeckillOrderTo();// 订单
                            order.setOrderSn(orderSn);// 订单号
                            order.setMemberId(user.getId());// 用户ID
                            order.setNum(num);// 商品上来给你
                            order.setPromotionSessionId(skuInfo.getPromotionSessionId());// 场次id
                            order.setSkuId(skuInfo.getSkuId());// 商品id
                            order.setSeckillPrice(skuInfo.getSeckillPrice());// 秒杀价格
                            // 8.需要保证可靠消息，发送者确认+消费者确认（本地事务的形式）
                            rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", order);
                            long end = System.currentTimeMillis();
                            log.info("秒杀成功，耗时..." + (end - start));
                            return orderSn;
                        }
                    }
                }
            }
        }
        long end = System.currentTimeMillis();
        log.info("秒杀失败，耗时..." + (end - start));
        return null;
    }
}
