package edu.neu.ecommerce.member.service;


import com.baomidou.mybatisplus.extension.service.IService;
import edu.neu.ecommerce.member.entity.MemberLoginLogEntity;
import edu.neu.ecommerce.utils.PageUtils;

import java.util.Map;

/**
 * 会员登录记录
 *
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 13:01:09
 */
public interface MemberLoginLogService extends IService<MemberLoginLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

