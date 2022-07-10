package edu.neu.ecommerce.statistics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync
@Configuration
public class ExecutorConfig {

    @Bean("asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        int corePoolSize = 3;
        executor.setCorePoolSize(corePoolSize);

        int maxPoolSize = 5;
        executor.setMaxPoolSize(maxPoolSize);

        int queueCapacity = 1;
        executor.setQueueCapacity(queueCapacity);

        int keepAliveSeconds = 60;
        executor.setKeepAliveSeconds(keepAliveSeconds);

        boolean allowCoreThreadTimeOut = false;
        executor.setAllowCoreThreadTimeOut(allowCoreThreadTimeOut);

        // 设置拒绝策略，直接在execute方法的调用线程中运行被拒绝的任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 执行初始化
        executor.initialize();
        return executor;
    }
}
