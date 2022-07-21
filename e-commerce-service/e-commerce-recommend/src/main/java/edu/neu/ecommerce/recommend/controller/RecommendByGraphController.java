package edu.neu.ecommerce.recommend.controller;

import edu.neu.ecommerce.recommend.neo4j.NeoClient;
import edu.neu.ecommerce.recommend.service.RecommendService;
import edu.neu.ecommerce.recommend.vo.SkuInfoVo;
import edu.neu.ecommerce.utils.R;
import edu.neu.ecommerce.vo.graph.ProductNodeVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>图推荐控制器类</h1>
 */
@RestController
@RequestMapping("/graph-recommend")
public class RecommendByGraphController {

    private final RecommendService recommendService;

    public RecommendByGraphController(RecommendService recommendService) {
        this.recommendService = recommendService;
    }

    @GetMapping("/also-buy")
    public R recommendByAlsoBuy(@RequestParam("skuId") Long skuId){
        List<ProductNodeVo> result;
        try (NeoClient client = new NeoClient("bolt://localhost:7687", "neo4j", "123456")) {
            result = client.getAlsoBuy(skuId);
        }
        List<Long> skuIds = result.stream().map(ProductNodeVo::getSkuId).collect(Collectors.toList());
        List<SkuInfoVo> skuInfoVos = recommendService.getRecommendSkuInfos(skuIds);
        return R.ok().setData(skuInfoVos);
    }
}
