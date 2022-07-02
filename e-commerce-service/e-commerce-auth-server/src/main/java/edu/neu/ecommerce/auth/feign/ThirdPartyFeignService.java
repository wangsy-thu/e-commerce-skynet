package edu.neu.ecommerce.auth.feign;

import edu.neu.ecommerce.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("e-commerce-third-party")
public interface ThirdPartyFeignService {
    @GetMapping("/sms/sendcode")
    R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
