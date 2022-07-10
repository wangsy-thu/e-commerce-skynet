package edu.neu.ecommerce.product.web;

import edu.neu.ecommerce.product.service.SkuInfoService;
import edu.neu.ecommerce.product.sink.SkuClickSink;
import edu.neu.ecommerce.product.vo.SkuItemVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

/**
 * <h1>商品详情Controller定义</h1>
 */
@Controller
@Slf4j
public class ItemController {

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuClickSink skuClickSink;

    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
        log.info("fetch item:[{}]", skuId);
        SkuItemVo vo = skuInfoService.item(skuId);
        skuClickSink.sinkSkuClick(skuId);
        model.addAttribute("item", vo);
        return "item";
    }
}
