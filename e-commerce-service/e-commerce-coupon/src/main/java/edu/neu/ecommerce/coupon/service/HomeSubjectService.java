package edu.neu.ecommerce.coupon.service;


import com.baomidou.mybatisplus.extension.service.IService;
import edu.neu.ecommerce.coupon.entity.HomeSubjectEntity;
import edu.neu.ecommerce.utils.PageUtils;

import java.util.Map;

/**
 * 首页专题表【jd首页下面很多专题，每个专题链接新的页面，展示专题商品信息】
 *
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 12:51:34
 */
public interface HomeSubjectService extends IService<HomeSubjectEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

