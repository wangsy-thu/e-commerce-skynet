package edu.neu.ecommerce.schedule.job;

/**
 * <h1>定时任务实现接口</h1>
 */
public interface ECommerceJob {

    /**
     * <h2>执行任务接口</h2>
     * @param params 参数：多参数使用JSON格式传递
     */
    void run(String params);
}
