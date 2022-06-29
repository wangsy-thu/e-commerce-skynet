package edu.neu.ecommerce.admin.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * <h1>监控服务器安全配置</h1>
 * 基于Spring Security
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /* 应用上下文  */

    private final String adminContextPath;

    public SecurityConfig(AdminServerProperties adminServerProperties){
        this.adminContextPath = adminServerProperties.getContextPath();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        SavedRequestAwareAuthenticationSuccessHandler successHandler =
                new SavedRequestAwareAuthenticationSuccessHandler();

        successHandler.setTargetUrlParameter("redirect");
        successHandler.setDefaultTargetUrl(adminContextPath + "/");

        http.authorizeRequests()
                //1. 配置所有的静态资源和登录页面可以公开访问
                .antMatchers(adminContextPath + "/assets/**").permitAll()
                .antMatchers(adminContextPath + "/login").permitAll()
                //2. 其他请求，必须通过认证
                .anyRequest().authenticated()
                .and()
                //3. 配置登录和登出的路径
                .formLogin().loginPage(adminContextPath + "/login").successHandler(successHandler)
                .and()
                .logout()
                .logoutUrl(adminContextPath + "/logout")
                .and()
                //4. 开启其他的HTTP basic支持，其他的服务模块注册时需要使用
                .httpBasic()
                .and()
                //5. 开启基于cookie的csrf保护
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                //6. 忽略这些路径的csrf保护 以便于其他的模块可以实现注册
                .ignoringAntMatchers(
                        adminContextPath + "/instance",
                        adminContextPath + "/actuator/**"
                );

    }
}
