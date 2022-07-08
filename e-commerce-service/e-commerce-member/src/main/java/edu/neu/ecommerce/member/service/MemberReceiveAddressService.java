package edu.neu.ecommerce.member.service;


import com.baomidou.mybatisplus.extension.service.IService;
import edu.neu.ecommerce.member.entity.MemberReceiveAddressEntity;
import edu.neu.ecommerce.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 会员收货地址
 *
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 13:01:09
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<MemberReceiveAddressEntity> getAddress(Long memberId);
}

