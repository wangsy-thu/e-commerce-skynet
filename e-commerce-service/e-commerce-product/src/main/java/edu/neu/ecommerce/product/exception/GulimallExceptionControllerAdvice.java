package edu.neu.ecommerce.product.exception;

import com.alibaba.fastjson.JSON;
import edu.neu.ecommerce.exception.BizCodeEnume;
import edu.neu.ecommerce.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>集中处理所有异常</h1>
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.atguigu.gulimall.product.controller")
public class GulimallExceptionControllerAdvice {


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e){
        log.error("数据校验出现问题{},异常类型{}", e.getMessage(),e.getClass());
        Map<String, String> errorMap = new HashMap<>();
        BindingResult bindingResult = e.getBindingResult();
        bindingResult.getFieldErrors().forEach(item -> errorMap.put(item.getField(), item.getDefaultMessage()));
        return R.error(BizCodeEnume.VALID_EXCEPTION.getCode(), "数据校验存在问题").put("data", errorMap);
    }

    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable){
        log.error("存在其他异常:{}", JSON.toJSONString(throwable.getMessage()));
        log.error("异常信息: " + throwable);
        return R.error(BizCodeEnume.UNKNOWN_EXCEPTION.getCode(), BizCodeEnume.UNKNOWN_EXCEPTION.getMsg());
    }

}
