package edu.neu.ecommerce.ware.feign;

import edu.neu.ecommerce.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-product")
public interface ProductFeignService {

    /**
     * 信息
     * 存在两种写法
     * 1.过网关，@FeignClient("gulimall-gateway")，然后所有请求前缀加/api/
     * 2.不过网关，@FeignClient("gulimall-product")，请求前缀不加/api/，直接访问模块
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);
}
