package edu.neu.ecommerce.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.neu.ecommerce.member.dao.MemberReceiveAddressDao;
import edu.neu.ecommerce.member.entity.MemberReceiveAddressEntity;
import edu.neu.ecommerce.member.service.MemberReceiveAddressService;
import edu.neu.ecommerce.utils.PageUtils;
import edu.neu.ecommerce.utils.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("memberReceiveAddressService")
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressDao, MemberReceiveAddressEntity> implements MemberReceiveAddressService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberReceiveAddressEntity> page = this.page(
                new Query<MemberReceiveAddressEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<MemberReceiveAddressEntity> getAddress(Long memberId) {
        return this.list(new QueryWrapper<MemberReceiveAddressEntity>()
                .eq("member_id", memberId));
    }

}