package edu.neu.ecommerce.crawler.controller;

import edu.neu.ecommerce.crawler.service.ResultSaveService;
import edu.neu.ecommerce.crawler.service.ResultSearchService;
import edu.neu.ecommerce.crawler.vo.CrawSearchParam;
import edu.neu.ecommerce.crawler.vo.CrawSearchResult;
import edu.neu.ecommerce.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * <h1>爬取数据控制器类</h1>
 */
@RestController
@Slf4j
@RequestMapping("/craw")
public class CrawResultController {


    private final ResultSearchService resultSearchService;

    private final ResultSaveService resultSaveService;

    public CrawResultController(ResultSearchService resultSearchService,
                                ResultSaveService resultSaveService) {
        this.resultSearchService = resultSearchService;
        this.resultSaveService = resultSaveService;
    }

    @GetMapping("/fetch")
    public R crawFromWeb(@RequestParam("keyword") String keyword){
        resultSaveService.crawSkuFromWeb(keyword);
        return R.ok();
    }

    @PostMapping("/search")
    public R search(@RequestBody CrawSearchParam param){
        CrawSearchResult result = resultSearchService.search(param);
        return R.ok().put("data", result);
    }
}
