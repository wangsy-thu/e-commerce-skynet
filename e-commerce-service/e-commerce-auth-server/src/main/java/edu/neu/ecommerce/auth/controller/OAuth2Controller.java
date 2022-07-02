package edu.neu.ecommerce.auth.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import edu.neu.ecommerce.auth.feign.MemberFeignService;
import edu.neu.ecommerce.auth.vo.MemberResponseVo;
import edu.neu.ecommerce.auth.vo.SocialUser;
import edu.neu.ecommerce.utils.HttpUtils;
import edu.neu.ecommerce.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 社交登录
 *
 * @Author: wanzenghui
 * @Date: 2021/11/26 22:26
 */
@Slf4j
@Controller
public class OAuth2Controller {

    @Autowired
    MemberFeignService memberFeignService;

    /**
     * 授权回调页
     *
     * @param code 根据code换取Access Token，且code只能兑换一次Access Token
     */
    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session,
                        HttpServletResponse servletResponse) throws Exception {
        // 1.根据code换取Access Token
        Map<String, String> headers = new HashMap<>();
        Map<String, String> querys = new HashMap<>();
        Map<String, String> map = new HashMap<>();
        map.put("client_id", "2129105835");
        map.put("client_secret", "201b8aa95794dbb6d52ff914fc8954dc");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://auth.gulimall.com/oauth2.0/weibo/success");
        map.put("code", code);
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", headers, querys, map);

        // 2.处理请求返回
        if (response.getStatusLine().getStatusCode() == 200) {
            // 换取Access_Token成功
            String jsonString = EntityUtils.toString(response.getEntity());
            SocialUser user = JSONObject.parseObject(jsonString, SocialUser.class);

            // 首次登录自动注册（为当前社交登录用户生成一个会员账号信息，以后这个社交账户就会对应指定的会员）
            // 非首次登录则直接登录成功
            R r = memberFeignService.oauthLogin(user);
            if (r.getCode() == 0) {
                // 登录成功
                MemberResponseVo loginUser = r.getData("data", new TypeReference<MemberResponseVo>() {
                });
                log.info("登录成功：用户：{}", loginUser.toString());
                session.setAttribute("loginUser", loginUser);
                // 3.信息存储到session中，并且放大作用域（指定domain=父级域名）
                return "redirect:http://gulimall.com";
            } else {
                // 登录失败，调回登录页
                return "redirect:http://auth.gulimall.com/login.html";
            }
        } else {
            // 换取Access_Token成功
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }


}
