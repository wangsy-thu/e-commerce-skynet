package edu.neu.ecommerce.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.neu.ecommerce.order.entity.OrderEntity;
import edu.neu.ecommerce.order.vo.*;
import edu.neu.ecommerce.to.mq.SeckillOrderTo;
import edu.neu.ecommerce.utils.PageUtils;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 13:14:28
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    SubmitOrderResponseVo submitOrder(OrderSubmitVo vo);

    OrderEntity getOrderByOrderSn(String orderSn);

    void closeOrder(OrderEntity orderEntity);

    PayVo getOrderPay(String orderSn);

    PageUtils queryPageWithItem(Map<String, Object> params);

    String handlePayResult(PayAsyncVo asyncVo);

    /**
     * 创建秒杀订单
     * @param order 秒杀订单信息
     */
    void createSeckillOrder(SeckillOrderTo order);
}

