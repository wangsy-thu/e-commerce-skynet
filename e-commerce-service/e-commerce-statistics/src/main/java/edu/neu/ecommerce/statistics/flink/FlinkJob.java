package edu.neu.ecommerce.statistics.flink;

import com.alibaba.fastjson.JSON;
import edu.neu.ecommerce.vo.SkuClickVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component
@Slf4j
public class FlinkJob implements CommandLineRunner {

    private final SinkMysql sinkMysql;

    private final Executor executor;

    public FlinkJob(SinkMysql sinkMysql, Executor executor) {
        this.sinkMysql = sinkMysql;
        this.executor = executor;
    }

    @Override
    public void run(String... args) {
        executor.execute(() -> {
            try {
                fireFlink();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        log.info(">>>>>>>>>>>>> flink job fired >>>>>>>>>>>>>");
    }

    public void fireFlink() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        RMQConnectionConfig connectionConfig = new RMQConnectionConfig.Builder()
                .setHost("127.0.0.1")
                .setPort(5672)
                .setUserName("guest")
                .setPassword("guest")
                .setVirtualHost("/")
                .build();

        DataStream<String> dataStreamSource = env
                .addSource(new RMQSource<>(connectionConfig, "tutu", true, new SimpleStringSchema()))
                .setParallelism(1);

        MapFunction<String, SkuClickVo> mapFunction = s -> JSON.parseObject(s, SkuClickVo.class);
        //数据转换
        DataStream<SkuClickVo> skuClickVoDataStream = dataStreamSource.map(mapFunction);

        skuClickVoDataStream.addSink(sinkMysql);
        env.execute();
    }
}
