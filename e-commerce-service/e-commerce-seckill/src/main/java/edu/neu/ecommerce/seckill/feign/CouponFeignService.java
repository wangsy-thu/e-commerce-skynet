package edu.neu.ecommerce.seckill.feign;

import edu.neu.ecommerce.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("e-commerce-coupon")
public interface CouponFeignService {

    @GetMapping(value = "/coupon/seckillsession/Lates3DaySession")
    R getLates3DaySession();

}
