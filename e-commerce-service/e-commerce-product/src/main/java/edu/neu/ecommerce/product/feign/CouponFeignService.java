package edu.neu.ecommerce.product.feign;


import edu.neu.ecommerce.to.SkuReductionTo;
import edu.neu.ecommerce.to.SpuBoundsTo;
import edu.neu.ecommerce.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("e-commerce-coupon")
public interface CouponFeignService {

    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundsTo spuBoundsTo);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
