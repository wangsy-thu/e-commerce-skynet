package edu.neu.ecommerce.seckill.feign;

import edu.neu.ecommerce.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("e-commerce-product")
public interface ProductFeignService {

    /**
     * 信息
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfo(@PathVariable("skuId") Long skuId);

    /**
     * 查询商品详情
     * list
     */
    @PostMapping("/product/skuinfo/infos")
    R getSkuInfos(@RequestBody List<Long> skuIds);
}
