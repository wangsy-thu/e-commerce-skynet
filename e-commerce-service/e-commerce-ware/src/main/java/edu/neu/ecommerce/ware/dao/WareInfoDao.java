package edu.neu.ecommerce.ware.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.neu.ecommerce.ware.entity.WareInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库信息
 * 
 * @author WangY
 * @email 1178663139@qq.com
 * @date 2022-05-29 13:22:06
 */
@Mapper
public interface WareInfoDao extends BaseMapper<WareInfoEntity> {
	
}
