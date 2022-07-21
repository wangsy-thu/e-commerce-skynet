package edu.neu.ecommerce.recommend.service.impl;

import com.alibaba.fastjson.TypeReference;
import edu.neu.ecommerce.recommend.feign.ProductFeignService;
import edu.neu.ecommerce.recommend.interceptor.CartInterceptor;
import edu.neu.ecommerce.recommend.service.RecommendService;
import edu.neu.ecommerce.recommend.to.UserInfoTo;
import edu.neu.ecommerce.recommend.vo.SkuInfoVo;
import edu.neu.ecommerce.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("recommendService")
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    ProductFeignService productFeignService;

    @Override
    public List<SkuInfoVo> getRecommendSkuInfos(List<Long> skuIds) {
        List<SkuInfoVo> skuInfoVos = null;
        R info = productFeignService.getSkuInfos(skuIds);
        if (info.getCode() == 0) {
            // 将查询结果封装成List集合
//            Map<Long, SkuInfoVo> skuMap = info.getData(new TypeReference<List<SkuInfoVo>>() {
//            }).stream().collect(Collectors.toMap(SkuInfoVo::getSkuId, val -> val));
            skuInfoVos = info.getData(new TypeReference<List<SkuInfoVo>>() {
            }).stream().collect(Collectors.toList());

        }
        return skuInfoVos;
    }

    @Override
    public Long getUserId() {
//        return (long)1;
        // 获取用户登录信息
        UserInfoTo userInfo = CartInterceptor.threadLocal.get();
        System.out.println("userInfo"+userInfo);
        if (userInfo != null && userInfo.getUserId() != null) {
            // 登录态，返回用户id
            return userInfo.getUserId();
        } else {
            // 非登录态，默认用户为1
            return (long)1;
        }
    }
}
