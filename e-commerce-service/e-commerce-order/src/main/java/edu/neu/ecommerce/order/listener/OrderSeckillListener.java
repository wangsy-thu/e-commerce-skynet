package edu.neu.ecommerce.order.listener;

import com.rabbitmq.client.Channel;
import edu.neu.ecommerce.order.service.OrderService;
import edu.neu.ecommerce.to.mq.SeckillOrderTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 创建秒杀订单监听器
 */
@Slf4j
@RabbitListener(queues = "order.seckill.order.queue")
@Component
public class OrderSeckillListener {

    @Autowired
    private OrderService orderService;

    /**
     * 秒杀成功异步通知
     * 创建订单
     * @param order 秒杀订单信息
     */
    @RabbitHandler
    public void listener(SeckillOrderTo order, Channel channel, Message message) throws IOException {
        log.info("准备创建秒杀单的详细信息...");
        try {
            orderService.createSeckillOrder(order);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
}
