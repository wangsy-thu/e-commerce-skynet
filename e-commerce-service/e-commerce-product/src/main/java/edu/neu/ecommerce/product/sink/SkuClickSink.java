package edu.neu.ecommerce.product.sink;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import edu.neu.ecommerce.vo.SkuClickVo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <h1>用户点击商品输出</h1>
 */
@Component
public class SkuClickSink {

    private final RabbitTemplate rabbitTemplate;

    public SkuClickSink(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sinkSkuClick(Long skuId){
        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        SkuClickVo skuClickVo = new SkuClickVo(0L, skuId, 1L, timeStr);
        rabbitTemplate.convertAndSend("tutu", skuClickVo);
    }
}
