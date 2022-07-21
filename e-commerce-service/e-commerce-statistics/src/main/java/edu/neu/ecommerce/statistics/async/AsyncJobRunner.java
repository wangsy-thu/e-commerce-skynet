package edu.neu.ecommerce.statistics.async;

import edu.neu.ecommerce.statistics.flink.FlinkJob;
import edu.neu.ecommerce.statistics.flink.job.ClickFlinkJob;
import edu.neu.ecommerce.statistics.flink.job.OrderFlinkJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

/**
 * <h1>异步任务启动器</h1>
 */
@Component
@Slf4j
public class AsyncJobRunner implements CommandLineRunner {

    private final FlinkJob clickFlinkJob;

    private final FlinkJob orderFlinkJob;

    private final Executor asyncExecutor;

    public AsyncJobRunner(ClickFlinkJob clickFlinkJob,
                          OrderFlinkJob orderFlinkJob,
                          Executor asyncExecutor) {
        this.clickFlinkJob = clickFlinkJob;
        this.orderFlinkJob = orderFlinkJob;
        this.asyncExecutor = asyncExecutor;
    }

    @Override
    public void run(String... args) {
        asyncExecutor.execute(() -> {
            try {
                clickFlinkJob.fireFlink();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        asyncExecutor.execute(() -> {
            try {
                orderFlinkJob.fireFlink();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        log.info(">>>>>>>>>>>>> all flink job fired >>>>>>>>>>>>>");
    }
}
