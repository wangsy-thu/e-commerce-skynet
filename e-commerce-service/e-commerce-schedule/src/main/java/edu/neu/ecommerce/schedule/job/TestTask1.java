package edu.neu.ecommerce.schedule.job;

import org.springframework.stereotype.Component;

@Component("testTask1")
public class TestTask1 implements ECommerceJob{
    @Override
    public void run(String params) {
        System.out.println("定时任务1执行:" + params);
    }
}
