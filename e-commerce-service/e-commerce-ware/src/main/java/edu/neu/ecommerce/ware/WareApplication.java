package edu.neu.ecommerce.ware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * <h1>仓储微服务启动类</h1>
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "edu.neu.ecommerce.ware.feign")
public class WareApplication {
    public static void main(String[] args) {
        SpringApplication.run(WareApplication.class, args);
    }
}
