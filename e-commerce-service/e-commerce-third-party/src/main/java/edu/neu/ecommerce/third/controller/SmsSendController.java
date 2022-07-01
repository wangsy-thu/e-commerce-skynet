package edu.neu.ecommerce.third.controller;

import edu.neu.ecommerce.third.component.SmsComponent;
import edu.neu.ecommerce.utils.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <h1>短信验证码发送Controller</h1>
 */
@RestController
@RequestMapping("/sms")
public class SmsSendController {

    @Resource
    private SmsComponent smsComponent;

    /**
     * <h2>发送验证码</h2>
     * @param phone 电话号码
     * @param code 验证码
     * @return 发送结果
     */
    @GetMapping("/sendcode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code){
        smsComponent.sendSmsCode(phone, code);
        return R.ok();
    }
}
