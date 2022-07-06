package edu.neu.ecommerce.seckill.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

// 开启异步任务
@EnableAsync
// 开启定时任务
@EnableScheduling
// 配置类
@Configuration
public class ScheduledConfig {
}
