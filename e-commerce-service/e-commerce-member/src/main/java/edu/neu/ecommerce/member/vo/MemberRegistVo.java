package edu.neu.ecommerce.member.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>用户注册值对象定义</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRegistVo {

    private String userName;

    private String passWord;

    private String phone;
}
