package edu.neu.ecommerce.schedule.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <h1>测试定时任务</h1>
 */
@Component("testTask")
public class TestTask implements ECommerceJob{
    @Override
    public void run(String params){
        System.out.println("定时任务执行:" + params);
    }
}
