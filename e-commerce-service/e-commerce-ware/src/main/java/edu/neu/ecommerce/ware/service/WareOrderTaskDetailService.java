package edu.neu.ecommerce.ware.service;


import com.baomidou.mybatisplus.extension.service.IService;
import edu.neu.ecommerce.utils.PageUtils;
import edu.neu.ecommerce.ware.entity.WareOrderTaskDetailEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 13:22:06
 */
public interface WareOrderTaskDetailService extends IService<WareOrderTaskDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

