package edu.neu.ecommerce.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import edu.neu.ecommerce.auth.feign.MemberFeignService;
import edu.neu.ecommerce.auth.feign.ThirdPartyFeignService;
import edu.neu.ecommerce.auth.vo.UserLoginVo;
import edu.neu.ecommerce.auth.vo.UserRegistVo;
import edu.neu.ecommerce.constant.AuthServerConstant;
import edu.neu.ecommerce.exception.BizCodeEnume;
import edu.neu.ecommerce.utils.R;
import edu.neu.ecommerce.vo.MemberResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class LoginController {

    @Resource
    private ThirdPartyFeignService thirdPartyFeignService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MemberFeignService memberFeignService;

    @GetMapping("/sms/sendcode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone) {
        // 1. 接口防刷
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (redisCode != null) {
            long l = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - l < 60 * 1000) {
                // 60s内不能再发
                return R.error(BizCodeEnume.SMS_CODE_EXCEPTION.getCode(), BizCodeEnume.SMS_CODE_EXCEPTION.getMsg());
            }
        }

        // 2. 验证码再次校验：存储到 Redis 中，存 key-phone，值 value-code
        String code = UUID.randomUUID().toString().substring(0, 5) + "_" + System.currentTimeMillis();
        // Redis 缓存验证码，防止同一个手机号在60s内再次发送
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone, code, 10, TimeUnit.MINUTES);
        thirdPartyFeignService.sendCode(phone, code.substring(0, 5));
        return R.ok();
    }

    @PostMapping("/regist")
    public String register(@Valid UserRegistVo vo,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            handleValidationError(bindingResult, redirectAttributes);
            return "redirect:/reg.html";
        }

        if (validateAndRegister(vo, redirectAttributes)) {
            return "redirect:/login.html";
        } else {
            return "redirect:/reg.html";
        }
    }

    private void handleValidationError(BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        Map<String, String> errors = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(
                FieldError::getField,
                FieldError::getDefaultMessage
        ));
        redirectAttributes.addFlashAttribute("errors", errors);
    }

    private boolean validateAndRegister(UserRegistVo vo, RedirectAttributes redirectAttributes) {
        String code = vo.getCode();
        String phone = vo.getPhone();
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);

        if (StringUtils.isEmpty(redisCode) || !code.equals(redisCode.split("_")[0])) {
            handleValidationError("code", "验证码错误", redirectAttributes);
            return false;
        }

        redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        R r = memberFeignService.regist(vo);
        if (r.getCode() == 0) {
            return true;
        } else {
            handleValidationError("message", r.getData("msg", new TypeReference<String>(){}), redirectAttributes);
            return false;
        }
    }

    private void handleValidationError(String fieldName, String errorMessage, RedirectAttributes redirectAttributes) {
        Map<String, String> errors = new HashMap<>();
        errors.put(fieldName, errorMessage);
        redirectAttributes.addFlashAttribute("errors", errors);
    }

    @GetMapping("/login.html")
    public String loginPage(HttpSession session) {
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute == null) {
            return "login";
        }
        return "redirect:/";
    }

    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes, HttpSession session) {
        log.info("get user:[{}]", JSON.toJSONString(vo));
        R login = memberFeignService.login(vo);
        if (login.getCode() == 0) {
            MemberResponseVo data = login.getData("data", new TypeReference<MemberResponseVo>() {});
            session.setAttribute(AuthServerConstant.LOGIN_USER, data);
            return "redirect:/";
        } else {
            handleValidationError("msg", login.getData("msg", new TypeReference<String>(){}), redirectAttributes);
            return "redirect:/login.html";
        }
    }
}
