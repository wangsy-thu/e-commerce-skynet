package edu.neu.ecommerce.product.feign.fallback;

import edu.neu.ecommerce.common.exception.BizCodeEnume;
import edu.neu.ecommerce.common.utils.R;
import edu.neu.ecommerce.product.feign.SeckillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SeckillFeignServiceFallBack implements SeckillFeignService {
    @Override
    public R getSkuSeckillInfo(Long skuId) {
        log.info("getSkuSeckillInfo 熔断方法调用...");
        return R.error(BizCodeEnume.TOO_MANY_REQUEST.getCode(), BizCodeEnume.TOO_MANY_REQUEST.getMsg());
    }
}
