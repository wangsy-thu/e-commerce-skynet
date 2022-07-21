package edu.neu.ecommerce.recommend.feign;

import edu.neu.ecommerce.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("e-commerce-product")
public interface ProductFeignService {

    /**
     * 查询商品详情
     * list
     */
    @PostMapping("/product/skuinfo/infos")
    R getSkuInfos(@RequestBody List<Long> skuIds);
}
