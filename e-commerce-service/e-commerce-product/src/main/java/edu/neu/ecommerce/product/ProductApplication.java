package edu.neu.ecommerce.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * <h1>商品微服务启动类</h1>
 */
@EnableCaching
@EnableRedisHttpSession
@SpringBootApplication
@MapperScan("edu.neu.ecommerce.product.dao")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "edu.neu.ecommerce.product.feign")

public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}
