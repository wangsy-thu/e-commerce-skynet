package edu.neu.ecommerce.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * springsession配置类
 */
@EnableRedisHttpSession
@Configuration
public class GulimallSessionConfig {
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setDomainName("gulimall.com");// 放大作用域
        cookieSerializer.setCookieName("GULISESSION");
        cookieSerializer.setCookieMaxAge(60 * 60 * 24 * 7);// 指定cookie有效期7天，会话级关闭浏览器后cookie即失效
        return cookieSerializer;
    }

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        // 指定session序列化到redis的序列化器
//        return new Jackson2JsonRedisSerializer<Object>(Object.class);// 无法保存对象类型，反序列化后默认使用Map封装
        return new GenericJackson2JsonRedisSerializer();
    }
}
