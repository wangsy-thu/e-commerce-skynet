package edu.neu.ecommerce.seckill.service;


import edu.neu.ecommerce.seckill.to.SeckillSkuRedisTo;

import java.util.List;

public interface SeckillService {

    /**
     * 上架最近三天需要秒杀的商品
     */
    void uploadSeckillSkuLatest3Days();

    /**
     * 查询当前时间可以参与秒杀的商品列表
     */
    List<SeckillSkuRedisTo> getCurrentSeckillSkus();

    /**
     * 根据skuId查询商品当前时间秒杀信息
     */
    SeckillSkuRedisTo getSkuSeckilInfo(Long skuId);

    /**
     * 秒杀商品
     * 1.校验登录状态
     * 2.校验秒杀时间
     * 3.校验随机码、场次、商品对应关系
     * 4.校验信号量扣减，校验购物数量是否限购
     * 5.校验是否重复秒杀（幂等性）【秒杀成功SETNX占位  userId-sessionId-skuId】
     * 6.扣减信号量
     * 7.发送消息，创建订单号和订单信息
     * 8.订单模块消费消息，生成订单
     * @param killId    sessionId_skuid
     * @param key   随机码
     * @param num   商品件数
     */
    String kill(String killId, String key, Integer num) throws InterruptedException;
}
