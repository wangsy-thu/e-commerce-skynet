package edu.neu.ecommerce.product.web;

import edu.neu.ecommerce.product.entity.CategoryEntity;
import edu.neu.ecommerce.product.service.CategoryService;
import edu.neu.ecommerce.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * <h1>页面跳转逻辑</h1>
 */
@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redisson;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model){

        //TODO 1,查出所有的分类数据
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categorys();
        model.addAttribute("categorys", categoryEntities);
        //利用视图解析器进行拼串
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatalogJson(){
        return categoryService.getCatelogJson();
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        //1，获取一把锁，只要锁名称一样，就是同一把锁
        RLock lock = redisson.getLock("my-lock");
        lock.lock();
        try{
            System.out.println("加锁成功，执行业务" + Thread.currentThread().getName());
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("释放锁");
            lock.unlock();
        }
        return "hello";
    }
}
