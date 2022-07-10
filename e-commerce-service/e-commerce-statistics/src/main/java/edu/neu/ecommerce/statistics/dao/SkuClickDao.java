package edu.neu.ecommerce.statistics.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.neu.ecommerce.statistics.entity.SkuClickEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SkuClickDao extends BaseMapper<SkuClickEntity> {
}
