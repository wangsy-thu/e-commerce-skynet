package edu.neu.ecommerce.statistics.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import edu.neu.ecommerce.utils.R;
import edu.neu.ecommerce.vo.SkuClickCountVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <h1>Sku TopN控制器定义</h1>
 */
@RestController
@RequestMapping("/statistics/click")
public class SkuTopNController {

    private final StringRedisTemplate redisTemplate;

    public SkuTopNController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/topN")
    public R clickTopN(){
        String listStr = redisTemplate.opsForValue().get("sku:click:topN");
        List<SkuClickCountVo> skuClickCountVos = JSON.parseObject(listStr, new TypeReference<List<SkuClickCountVo>>() {
        });
        return R.ok().put("data", skuClickCountVos);
    }
}
