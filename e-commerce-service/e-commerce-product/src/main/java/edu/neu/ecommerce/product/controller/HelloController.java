package edu.neu.ecommerce.product.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1>Web MVC测试Controller</h1>
 */
@RestController
@RequestMapping("/hello")
public class HelloController {

    @GetMapping("/world")
    public String world(){
        return "hello world";
    }
}
