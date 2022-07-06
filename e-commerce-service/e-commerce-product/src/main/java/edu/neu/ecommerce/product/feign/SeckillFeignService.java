package edu.neu.ecommerce.product.feign;

import edu.neu.ecommerce.common.utils.R;
import edu.neu.ecommerce.product.feign.fallback.SeckillFeignServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "e-commerce-seckill", fallback = SeckillFeignServiceFallBack.class)
public interface SeckillFeignService {

    @GetMapping(value = "/sku/seckill/{skuId}")
    R getSkuSeckillInfo(@PathVariable("skuId") Long skuId);

}
