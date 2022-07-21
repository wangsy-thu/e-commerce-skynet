package edu.neu.ecommerce.recommend.service;

import edu.neu.ecommerce.recommend.vo.SkuInfoVo;

import java.util.List;

public interface RecommendService {

    /**
     * 根据ids得到推荐的商品信息
     * @param result
     * @return
     */
    List<SkuInfoVo> getRecommendSkuInfos(List<Long> result);

    /**
     * 得到当前用户的id
     * @return
     */
    Long getUserId();

}
