package edu.neu.ecommerce.statistics.listener;

import com.alibaba.fastjson.JSON;
import edu.neu.ecommerce.vo.OrderSumVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static edu.neu.ecommerce.statistics.constant.StatisticsConstant.ORDER_SUM_LIST;

@Slf4j
@Service
public class RealTimePriceListener {

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();

    @RabbitListener(queues = "sku.order.realtime.price")
    public void listener(String orderSumVoStr){
        log.info("fetch rank topN:[{}]", orderSumVoStr);
        OrderSumVo result = JSON.parseObject(orderSumVoStr, OrderSumVo.class);
        writeLock.lock();
        try{
            ORDER_SUM_LIST.add(result);
            ORDER_SUM_LIST.remove(0);
        } finally {
            writeLock.unlock();
        }
    }
}
