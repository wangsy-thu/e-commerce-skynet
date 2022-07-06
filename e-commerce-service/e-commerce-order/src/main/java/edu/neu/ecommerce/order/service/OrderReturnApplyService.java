package edu.neu.ecommerce.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.neu.ecommerce.order.entity.OrderReturnApplyEntity;
import edu.neu.ecommerce.utils.PageUtils;

import java.util.Map;

/**
 * 订单退货申请
 *
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 13:14:28
 */
public interface OrderReturnApplyService extends IService<OrderReturnApplyEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

