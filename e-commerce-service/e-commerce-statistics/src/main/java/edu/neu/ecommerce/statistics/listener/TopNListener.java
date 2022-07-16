package edu.neu.ecommerce.statistics.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import edu.neu.ecommerce.vo.SkuClickCountVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <h1>排名信息监听器</h1>
 */
@Service
@Slf4j
public class TopNListener {

    private final StringRedisTemplate redisTemplate;

    public TopNListener(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @RabbitListener(queues = "sku.click.topN")
    public void listener(String skuClickCountVosStr){
        log.info("fetch rank topN:[{}]", skuClickCountVosStr);
        List<SkuClickCountVo> resultList = JSON.parseObject(skuClickCountVosStr, new TypeReference<List<SkuClickCountVo>>() {
        });
        redisTemplate.opsForValue().set("sku:click:topN", JSON.toJSONString(resultList));
    }
}
