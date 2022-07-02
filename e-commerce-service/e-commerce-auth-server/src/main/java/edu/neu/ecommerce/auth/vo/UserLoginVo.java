package edu.neu.ecommerce.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>用户登录VO</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginVo {
    /* 用户长账号 */
    private String loginacct;

    /* 用户密码 */
    private String password;
}
