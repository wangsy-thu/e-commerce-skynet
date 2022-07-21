package edu.neu.ecommerce.statistics.flink;

/**
 * <h1>Flink任务统一接口</h1>
 */
public interface FlinkJob {

    /**
     * <h1>Flink任务启动函数</h1>
     */
    void fireFlink() throws Exception;
}
