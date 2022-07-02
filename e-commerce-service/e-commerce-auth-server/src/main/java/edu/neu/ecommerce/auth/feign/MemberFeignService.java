package edu.neu.ecommerce.auth.feign;

import edu.neu.ecommerce.auth.vo.SocialUser;
import edu.neu.ecommerce.auth.vo.UserLoginVo;
import edu.neu.ecommerce.auth.vo.UserRegistVo;
import edu.neu.ecommerce.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <h1>用户服务远程接口</h1>
 */
@FeignClient("e-commerce-member")
public interface MemberFeignService {
    @PostMapping("/member/member/regist")
    R regist(@RequestBody UserRegistVo vo);

    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo vo);

    /**
     * 微博社交登录
     */
    @PostMapping("/member/member/oauth2/login")
    public R oauthLogin(@RequestBody SocialUser user);
}
