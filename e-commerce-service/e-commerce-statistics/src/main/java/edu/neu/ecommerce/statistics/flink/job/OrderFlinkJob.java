package edu.neu.ecommerce.statistics.flink.job;

import com.alibaba.fastjson.JSON;
import edu.neu.ecommerce.statistics.flink.FlinkJob;
import edu.neu.ecommerce.vo.OrderStatisticsVo;
import edu.neu.ecommerce.vo.OrderSumVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSink;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import org.apache.flink.util.Collector;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * <h1>订单统计Flink任务</h1>
 */
@Slf4j
@Component("orderFlinkJob")
public class OrderFlinkJob implements FlinkJob {

    @Override
    public void fireFlink() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        RMQConnectionConfig connectionConfig = new RMQConnectionConfig.Builder()
                .setHost("127.0.0.1")
                .setPort(5672)
                .setUserName("guest")
                .setPassword("guest")
                .setVirtualHost("/")
                .build();

        /* 配置Flink流数据源 */
        DataStreamSource<String> dataStreamSource = env
                .addSource(new RMQSource<>(connectionConfig, "flink.sku.order", true, new SimpleStringSchema()));

        MapFunction<String, OrderStatisticsVo> mapFunction = s -> JSON.parseObject(s, OrderStatisticsVo.class);

        DataStream<OrderStatisticsVo> orderStatisticsDataStream = dataStreamSource.map(mapFunction);
        orderStatisticsDataStream = orderStatisticsDataStream.assignTimestampsAndWatermarks(WatermarkStrategy.<OrderStatisticsVo>forMonotonousTimestamps()
                .withTimestampAssigner(new SerializableTimestampAssigner<OrderStatisticsVo>() {
                    @Override
                    public long extractTimestamp(OrderStatisticsVo element, long recordTimestamp) {
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String timeStr = element.getTimeStr();
                        return LocalDateTime.parse(timeStr, df)
                                .toInstant(ZoneOffset.of("+8")).toEpochMilli();
                    }
                }));

        SingleOutputStreamOperator<OrderSumVo> result = orderStatisticsDataStream
                .keyBy(OrderStatisticsVo::getTypeId)
                .window(SlidingEventTimeWindows.of(Time.seconds(5), Time.seconds(5)))
                .aggregate(new OrderStatisticsAgg(), new OrderPriceResult());

        SingleOutputStreamOperator<String> resultSinkDataStream = result.map(JSON::toJSONString);
        resultSinkDataStream.addSink(new RMQSink<>(connectionConfig, "sku.order.realtime.price", new SimpleStringSchema()));
        env.execute();
    }

    /**
     * <h2>基于BigDecimal的累加器</h2>
     */
    public static class OrderStatisticsAgg implements AggregateFunction<OrderStatisticsVo, BigDecimal, BigDecimal>{
        @Override
        public BigDecimal createAccumulator() {
            return BigDecimal.ZERO;
        }

        @Override
        public BigDecimal add(OrderStatisticsVo orderStatisticsVo, BigDecimal bigDecimal) {
            return bigDecimal.add(orderStatisticsVo.getPrice());
        }

        @Override
        public BigDecimal getResult(BigDecimal bigDecimal) {
            return bigDecimal;
        }

        @Override
        public BigDecimal merge(BigDecimal bigDecimal, BigDecimal acc1) {
            return null;
        }
    }

    /**
     * <h2>全窗口处理函数</h2>
     */
    public static class OrderPriceResult
            extends ProcessWindowFunction<BigDecimal, OrderSumVo, Long, TimeWindow>{
        @Override
        public void process(Long aLong, ProcessWindowFunction<BigDecimal, OrderSumVo, Long, TimeWindow>.Context context, Iterable<BigDecimal> elements, Collector<OrderSumVo> out) {
            Long start = context.window().getStart();
            Long end = context.window().getEnd();
            out.collect(new OrderSumVo(1L, elements.iterator().next(),start, end));
        }
    }

}
