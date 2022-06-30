package edu.neu.ecommerce.product.service;


import com.baomidou.mybatisplus.extension.service.IService;
import edu.neu.ecommerce.product.entity.CommentReplayEntity;
import edu.neu.ecommerce.utils.PageUtils;

import java.util.Map;

/**
 * 商品评价回复关系
 *
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 11:48:46
 */
public interface CommentReplayService extends IService<CommentReplayEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

