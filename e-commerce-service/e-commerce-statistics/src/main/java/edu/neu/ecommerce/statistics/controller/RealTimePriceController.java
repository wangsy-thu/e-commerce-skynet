package edu.neu.ecommerce.statistics.controller;

import edu.neu.ecommerce.utils.R;
import edu.neu.ecommerce.vo.OrderSumVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static edu.neu.ecommerce.statistics.constant.StatisticsConstant.ORDER_SUM_LIST;

/**
 * <h1>实时销量统计</h1>
 */
@RestController
@RequestMapping("/statistics/price")
public class RealTimePriceController {

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();

    @GetMapping("/list")
    public R list(){
        List<OrderSumVo> resultList;
        readLock.lock();
        try{
            resultList = ORDER_SUM_LIST;
        } finally {
            readLock.unlock();
        }
        return R.ok().put("priceList", resultList);
    }
}
