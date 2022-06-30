package edu.neu.ecommerce.product.service;


import com.baomidou.mybatisplus.extension.service.IService;
import edu.neu.ecommerce.product.entity.SpuImagesEntity;
import edu.neu.ecommerce.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * spu图片
 *
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 11:48:46
 */
public interface SpuImagesService extends IService<SpuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveImages(Long id, List<String> images);
}

