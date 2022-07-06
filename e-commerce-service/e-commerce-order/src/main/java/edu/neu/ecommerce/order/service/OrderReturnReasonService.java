package edu.neu.ecommerce.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.neu.ecommerce.order.entity.OrderReturnReasonEntity;
import edu.neu.ecommerce.utils.PageUtils;

import java.util.Map;

/**
 * 退货原因
 *
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 13:14:28
 */
public interface OrderReturnReasonService extends IService<OrderReturnReasonEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

