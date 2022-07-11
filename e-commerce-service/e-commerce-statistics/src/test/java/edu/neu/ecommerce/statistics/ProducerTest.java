package edu.neu.ecommerce.statistics;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import edu.neu.ecommerce.vo.SkuClickVo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class ProducerTest {
    public final static String QUEUE_NAME="tutu";  //队列的名字
    public static void main(String[] args) throws IOException, TimeoutException {
        //建立链接工厂
        ConnectionFactory factory = new ConnectionFactory();

        //设置RabbitMQ相关信息
        factory.setHost("127.0.0.1");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPort(5672);

        Connection connection = factory.newConnection();
        Random random = new Random();


        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        while (true) {
            String str = JSON.toJSONString(new SkuClickVo(0L, (long) random.nextInt(8), 1L, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            channel.basicPublish("", QUEUE_NAME, null, str.getBytes(StandardCharsets.UTF_8));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
