package edu.neu.ecommerce.member.service;


import com.baomidou.mybatisplus.extension.service.IService;
import edu.neu.ecommerce.member.entity.MemberEntity;
import edu.neu.ecommerce.member.exception.PhoneExistException;
import edu.neu.ecommerce.member.exception.UsernameExistException;
import edu.neu.ecommerce.member.vo.MemberLoginVo;
import edu.neu.ecommerce.member.vo.MemberRegistVo;
import edu.neu.ecommerce.member.vo.SocialUser;
import edu.neu.ecommerce.utils.PageUtils;

import java.util.Map;

/**
 * 会员
 *
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 13:01:09
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * <h2>用户注册</h2>
     * @param vo 用户注册值对象定义
     */
    void regist(MemberRegistVo vo);

    void checkPhoneUnique(String phone) throws PhoneExistException;

    void checkUsernameUnique(String username) throws UsernameExistException;

    /**
     * <h2>用户登录</h2>
     * @param vo 账号密码VO
     * @return 用户信息
     */
    MemberEntity login(MemberLoginVo vo);

    /**
     * <h2>社交登录</h2>
     * @param vo 社交信息
     * @return 用户信息
     */
    MemberEntity login(SocialUser vo);
}

