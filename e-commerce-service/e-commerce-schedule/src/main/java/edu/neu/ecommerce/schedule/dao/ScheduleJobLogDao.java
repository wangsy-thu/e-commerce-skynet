package edu.neu.ecommerce.schedule.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.neu.ecommerce.schedule.entity.ScheduleJobLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * <h1>定时任务日志Dao</h1>
 */
@Mapper
public interface ScheduleJobLogDao extends BaseMapper<ScheduleJobLogEntity> {

}
