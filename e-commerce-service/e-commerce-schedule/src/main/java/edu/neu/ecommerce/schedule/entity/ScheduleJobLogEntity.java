package edu.neu.ecommerce.schedule.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * <h1>定时任务日志实体类定义</h1>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_schedule_job_log")
public class ScheduleJobLogEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /* 日志id */
    @TableId
    private Long logId;

    /* 任务id */
    private Long jobId;

    /* spring bean名称 */
    private String beanName;

    /* 参数 */
    private String params;

    /* 任务状态    0：成功    1：失败 */
    private Integer status;

    /* 失败信息 */
    private String error;

    /* 耗时(单位：毫秒) */
    private Integer times;

    /* 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
