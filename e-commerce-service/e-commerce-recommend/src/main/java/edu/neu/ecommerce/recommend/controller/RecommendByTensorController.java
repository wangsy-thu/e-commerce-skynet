package edu.neu.ecommerce.recommend.controller;

import com.alibaba.fastjson.JSONArray;
import edu.neu.ecommerce.recommend.service.RecommendService;
import edu.neu.ecommerce.recommend.util.RestTemplateUtil;
import edu.neu.ecommerce.recommend.vo.SkuInfoVo;
import edu.neu.ecommerce.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>DSSM推荐控制器类</h1>
 */
@RestController
@RequestMapping("/tensor-recommend")
public class RecommendByTensorController {

    // 处理的是content type [text/html;charset=utf-8]的数据
    private final RestTemplate restTemplate = RestTemplateUtil.getRestTemplate();

    @Autowired
    private RecommendService recommendService;

    // 调用python flask的推荐服务的训练模型
    @GetMapping("/train-model")
    public R recommendTrainModel(){
        String url = "http://127.0.0.1:2000/train_model/";
        Object result = restTemplate.getForEntity(url, Object.class).getBody();
        return R.ok().put("data", result);
    }

    // 调用python flask的推荐服务的猜你喜欢
    @GetMapping("/guess-like")
    public R recommendByGuessLike(){
        Long userId = recommendService.getUserId();
        String url = "http://127.0.0.1:2000/guess_like/" + userId + "/";
        List<Integer> skuIdsInteger = (ArrayList<Integer>) restTemplate.getForEntity(url, ArrayList.class).getBody();
        List<Long> skuIds = JSONArray.parseArray(skuIdsInteger.toString(),Long.class);
        List<SkuInfoVo> skuInfoVos = recommendService.getRecommendSkuInfos(skuIds);
        return R.ok().setData(skuInfoVos);
    }

}
