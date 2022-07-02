package edu.neu.ecommerce.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>社交登录VO定义</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialUser {
    private String access_token;
    private String remind_in;
    private long expires_in;
    private String uid;
    private String isRealName;
}
