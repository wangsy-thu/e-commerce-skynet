package edu.neu.ecommerce.statistics.flink;

import com.alibaba.fastjson.JSON;
import edu.neu.ecommerce.vo.SkuClickCountVo;
import edu.neu.ecommerce.vo.SkuClickVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSink;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import org.apache.flink.util.Collector;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
        env.setParallelism(1);
        RMQConnectionConfig connectionConfig = new RMQConnectionConfig.Builder()
                .setHost("127.0.0.1")
                .setPort(5672)
                .setUserName("guest")
                .setPassword("guest")
                .setVirtualHost("/")
                .build();

        DataStreamSource<String> dataStreamSource = env
                .addSource(new RMQSource<>(connectionConfig, "tutu", true, new SimpleStringSchema()));


        MapFunction<String, SkuClickVo> mapFunction = s -> JSON.parseObject(s, SkuClickVo.class);
        //数据转换后分配时间窗口和水位线
        DataStream<SkuClickVo> skuClickVoDataStream = dataStreamSource.map(mapFunction);
        skuClickVoDataStream = skuClickVoDataStream.assignTimestampsAndWatermarks(WatermarkStrategy.<SkuClickVo>forMonotonousTimestamps()
                .withTimestampAssigner(new SerializableTimestampAssigner<SkuClickVo>() {
                    @Override
                    public long extractTimestamp(SkuClickVo element, long recordTimestamp) {
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String timeStr = element.getClickTime();
                        return LocalDateTime.parse(timeStr, df)
                                .toInstant(ZoneOffset.of("+8")).toEpochMilli();
                    }
                }));

        skuClickVoDataStream.addSink(sinkMysql);

        /* 聚集操作 输出统计类 */
        SingleOutputStreamOperator<SkuClickCountVo> skuClickCountStream = skuClickVoDataStream
                .keyBy(SkuClickVo::getSkuId)
                .window(SlidingEventTimeWindows.of(Time.seconds(10), Time.seconds(5)))
                .aggregate(new SkuCountAgg(), new SkuClickCountResult());

        /* 排序处理，输出结果 */
        SingleOutputStreamOperator<List<SkuClickCountVo>> result = skuClickCountStream.keyBy(SkuClickCountVo::getWindowEnd)
                .process(new TopN(5));

        /* 转换成String */
        SingleOutputStreamOperator<String> rmqStringOutputStream = result.map(JSON::toJSONString);
        rmqStringOutputStream.addSink(new RMQSink<>(connectionConfig, "sku.click.topN", new SimpleStringSchema()));
        env.execute();
    }

    /**
     * <h2>定义累加器函数</h2>
     */
    public static class SkuCountAgg implements AggregateFunction<SkuClickVo, Long, Long> {
        @Override
        public Long createAccumulator() {
            /* 初始化累加器 */
            return 0L;
        }

        @Override
        public Long add(SkuClickVo skuClickVo, Long aLong) {
            /* 累加方法 */
            return aLong + 1;
        }

        @Override
        public Long getResult(Long aLong) {
            /* 获取当前值 */
            return aLong;
        }

        @Override
        public Long merge(Long aLong, Long acc1) {
            return null;
        }
    }

    /**
     * <h1>定义全窗口函数</h1>
     */
    public static class SkuClickCountResult
            extends ProcessWindowFunction<Long, SkuClickCountVo, Long, TimeWindow>{
        @Override
        public void process(Long aLong, ProcessWindowFunction<Long, SkuClickCountVo, Long, TimeWindow>.Context context, Iterable<Long> elements, Collector<SkuClickCountVo> out) {
            Long start = context.window().getStart();
            Long end = context.window().getEnd();
            out.collect(new SkuClickCountVo(aLong, elements.iterator().next(), start, end));
        }
    }

    /**
     * <h1>自定义处理函数</h1>
     */
    public static class TopN extends KeyedProcessFunction<Long, SkuClickCountVo, List<SkuClickCountVo>>{

        /* top N 参数 */
        private final Integer n;

        /* 状态列表 */
        private ListState<SkuClickCountVo> skuClickCountListState;

        public TopN(Integer n){
            this.n = n;
        }

        @Override
        public void open(Configuration parameters) {
            /* 初始化状态列表 */
            skuClickCountListState = getRuntimeContext().getListState(
                    new ListStateDescriptor<>("sku-click-count-list",
                            Types.POJO(SkuClickCountVo.class))
            );
        }

        @Override
        public void processElement(SkuClickCountVo value, KeyedProcessFunction<Long, SkuClickCountVo, List<SkuClickCountVo>>.Context ctx, Collector<List<SkuClickCountVo>> out) throws Exception {
            skuClickCountListState.add(value);
            ctx.timerService().registerEventTimeTimer(ctx.getCurrentKey() + 1);
        }

        @Override
        public void onTimer(long timestamp, KeyedProcessFunction<Long, SkuClickCountVo, List<SkuClickCountVo>>.OnTimerContext ctx, Collector<List<SkuClickCountVo>> out) throws Exception {
            ArrayList<SkuClickCountVo> skuClickCountArrayList = new ArrayList<>();
            /* 将统计数量暂存到列表中，便于排序处理 */
            for (SkuClickCountVo skuClickCountVo : skuClickCountListState.get()) {
                skuClickCountArrayList.add(skuClickCountVo);
            }

            /* 清空状态变量 */
            skuClickCountListState.clear();

            /* 输入到RabbitMQ的变量 */
            List<SkuClickCountVo> rmqResult = new ArrayList<>();

            /* 排序处理 */
            skuClickCountArrayList.sort((o1, o2) -> o2.getCount().intValue() - o1.getCount().intValue());

            /* 取前 N 名输出结果 */
            if(skuClickCountArrayList.size() < this.n){
                /* 防止窗口内没有数据 */
                int diffNum = n - skuClickCountArrayList.size();
                for (int i = 0; i < diffNum; i++) {
                    skuClickCountArrayList.add(new SkuClickCountVo(((long) i), 0L, timestamp, timestamp));
                    rmqResult.addAll(skuClickCountArrayList);
                }
            }else{
                StringBuilder result = new StringBuilder();
                result.append("========================================\n");
                result.append("窗口结束时间：").append(new Timestamp(timestamp - 1)).append("\n");
                for (int i = 0; i < this.n; i++) {
                    SkuClickCountVo skuClickCountVo = skuClickCountArrayList.get(i);
                    String info = "No." + (i + 1) + " "
                            + "skuId：" + skuClickCountVo.getSkuId() + " "
                            + "点击量：" + skuClickCountVo.getCount() + "\n";
                    result.append(info);
                    rmqResult.add(skuClickCountVo);
                }
                result.append("========================================\n");
                System.out.println(result);
            }
            /* 收集流数据 */
            out.collect(rmqResult);
        }
    }
}
