package edu.neu.ecommerce.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.neu.ecommerce.order.entity.PaymentInfoEntity;
import edu.neu.ecommerce.utils.PageUtils;

import java.util.Map;

/**
 * 支付信息表
 *
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 13:14:28
 */
public interface PaymentInfoService extends IService<PaymentInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

