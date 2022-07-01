package edu.neu.ecommerce.member.service;


import com.baomidou.mybatisplus.extension.service.IService;
import edu.neu.ecommerce.member.entity.MemberLevelEntity;
import edu.neu.ecommerce.utils.PageUtils;

import java.util.Map;

/**
 * 会员等级
 *
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 13:01:09
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

