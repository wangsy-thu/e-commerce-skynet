package edu.neu.ecommerce.recommend.controller;

import edu.neu.ecommerce.recommend.neo4j.NeoClient;
import edu.neu.ecommerce.utils.R;
import edu.neu.ecommerce.vo.graph.ProductNodeVo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <h1>图推荐控制器类</h1>
 */
@RestController
@RequestMapping("/graph-recommend")
public class RecommendByGraphController {

    @GetMapping("/also-buy")
    public R recommendByAlsoBuy(@RequestParam("skuId") Long skuId){
        List<ProductNodeVo> result;
        try (NeoClient client = new NeoClient("bolt://localhost:7687", "neo4j", "123456")) {
            result = client.getAlsoBuy(skuId);
        }
        return R.ok().put("data", result);
    }
}
