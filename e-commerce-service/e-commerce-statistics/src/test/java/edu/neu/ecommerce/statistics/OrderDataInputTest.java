package edu.neu.ecommerce.statistics;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import edu.neu.ecommerce.vo.OrderStatisticsVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class OrderDataInputTest {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();

        //设置RabbitMQ相关信息
        factory.setHost("127.0.0.1");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPort(5672);

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Random random = new Random();
        random.setSeed(113);
        channel.queueDeclare("flink.sku.order", true, false, false, null);
        while(true) {
            OrderStatisticsVo orderStatisticsVo = new OrderStatisticsVo();
            orderStatisticsVo.setPrice(new BigDecimal(random.nextInt(3000)));
            orderStatisticsVo.setTimeStr(LocalDateTime.now().format(df));
            orderStatisticsVo.setTypeId(1L);
            System.out.println("set Vo");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            channel.basicPublish("", "flink.sku.order", null, JSON.toJSONString(orderStatisticsVo).getBytes());
        }
        //关闭通道和连接
        //channel.close();
        //connection.close();

    }
}
