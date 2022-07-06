package edu.neu.ecommerce.seckill.scheduled;

/**
 * 定时任务
 *      1. @EnableScheduling 开启定时任务
 *      2. @Scheduled 开启一个定时任务
 *      3. 自动配置类 TaskSchedulingAutoConfiguration
 * 异步任务
 *      1. @EnableAsync 开启异步任务
 *      2. @Async 异步执行的方法上标注注解
 *      3. 自动配置类 TaskExecutionAutoConfiguration 属性绑定在TaskExecutionProperties
 */
//@Slf4j
//@Component
//@EnableAsync
//@EnableScheduling
public class HelloSchedule {

    /*
     * 1. Spring 中只有6位，不写年
     * 2. 在周几的位置，1-7代表周一到周日，或者MON-SUN
     * 3. 定时任务不用改阻塞，默认是阻塞的
     *     1） 科技让业务运行，以异步的方式，自己提交到线程池
     *         CompletableFuture.runAsync(()->{
     *             xxxxSeivice.hello();
     *         }.executor);
     *     2)  支持定时任务线程池，设置 TaskSchedulingProperties
     *         #调整线程池大小，但是在有些版本好使，有些版本不好使
     *         spring.task.scheduling.pool.size=5
     *     3)  让定时任务异步执行
     *         异步任务
     *         spring.task.execution.pool.core-size=5
     *         spring.task.execution.pool.max-size=50
     * 解决：使用异步+定时任务完成定时任务不阻塞的问题
     */
//    @Async
//    @Scheduled(cron = "* * * * * ?")
//    public void hello() throws InterruptedException {
//        log.info("hello...");
//        Thread.sleep(3000);
//
//    }

}
