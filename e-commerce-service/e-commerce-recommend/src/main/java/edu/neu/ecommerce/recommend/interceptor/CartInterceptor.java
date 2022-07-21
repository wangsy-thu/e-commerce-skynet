package edu.neu.ecommerce.recommend.interceptor;

import edu.neu.ecommerce.constant.AuthServerConstant;
import edu.neu.ecommerce.constant.CartConstant;
import edu.neu.ecommerce.recommend.to.UserInfoTo;
import edu.neu.ecommerce.vo.MemberResponseVo;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * 在执行目标方法之前，判断用户的登录状态，并封装传递给controller目标请求
 */
public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    /**
     * 目标方法执行之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取会话信息，获取登录用户信息
        HttpSession session = request.getSession();
        MemberResponseVo attribute = (MemberResponseVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        // 判断是否登录，并封装User对象给controller使用
        UserInfoTo user = new UserInfoTo();
        System.out.println("拦截器开始工作！！！");
        if (attribute != null) {
            // 登录状态，封装用户ID，供controller使用
            user.setUserId(attribute.getId());
        }
        // 获取当前请求游客用户标识user-key
        Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isNotEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(CartConstant.TEMP_USER_COOKIE_NAME)) {
                    // 获取user-key值封装到user，供controller使用
                    user.setUserKey(cookie.getValue());
                    user.setTempUser(true);// 不需要重新分配
                    break;
                }
            }
        }

        // 判断当前是否存在游客用户标识
        if (StringUtils.isBlank(user.getUserKey())) {
            // 无游客标识，分配游客标识
            user.setUserKey(UUID.randomUUID().toString());
        }

        // 封装用户信息（登录状态userId非空，游客状态userId空）
        threadLocal.set(user);
        return true;
    }

    /**
     * 业务执行之后，如果不是临时用户，让浏览器保存一个cookie，保存临时用户1个月
     * 分配临时用户，让浏览器保存
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo user = threadLocal.get();
        // 如果不是临时用户，一定要保存一个临时用户
        if (user != null && !user.isTempUser()) {
            // 需要为客户端分配游客信息
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, user.getUserKey());
            cookie.setDomain("gulimall.com");// 作用域
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);// 过期时间
            response.addCookie(cookie);
        }
    }

}
