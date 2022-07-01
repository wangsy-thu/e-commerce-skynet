package edu.neu.ecommerce.ware.service;


import com.baomidou.mybatisplus.extension.service.IService;
import edu.neu.ecommerce.utils.PageUtils;
import edu.neu.ecommerce.ware.entity.WareInfoEntity;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 13:22:06
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

