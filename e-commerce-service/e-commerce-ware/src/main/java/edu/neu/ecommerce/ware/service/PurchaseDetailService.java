package edu.neu.ecommerce.ware.service;


import com.baomidou.mybatisplus.extension.service.IService;
import edu.neu.ecommerce.utils.PageUtils;
import edu.neu.ecommerce.ware.entity.PurchaseDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author wanzenghui
 * @email lemon_wan@aliyun.com
 * @date 2021-09-02 22:59:35
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    /**
     * 商品库存（可根据skuId、wareId查找）
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据采购单ID查询采购需求
     */
    List<PurchaseDetailEntity> listDetailByPurchaseId(Long purchaseId);
}

