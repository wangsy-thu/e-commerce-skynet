package edu.neu.ecommerce.statistics.flink;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import edu.neu.ecommerce.vo.SkuClickVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class SinkMysql extends RichSinkFunction<SkuClickVo> {
    private PreparedStatement ps;
    private Connection connection;

    /**
     * open() 方法中创建链接，这样不用每次 invoke 的时候都要创建链接和释放链接
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        Class.forName("com.mysql.jdbc.Driver");//加载数据库驱动
        connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ecommerce_statistics?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai", "root", "root");//获取链接
        //设置链接池的一些参数
    }

    @Override
    public void close() throws Exception {
        super.close();
        //关闭链接和释放资源
        if (connection != null) {
            connection.close();
        }
        if (ps != null) {
            ps.close();
        }
    }

    /**
     * 每条数据的插入都要调用一次 invoke()
     */
    @Override
    public void invoke(SkuClickVo value, Context context) throws Exception {
        //获取对象，换成本身对象，或者什么string，随意。

        ps = connection.prepareStatement("insert into tb_click_log(" +
                "     sku_id ,\n" +
                "     user_id , \n" +
                "     click_time \n" +
                " ) values(?,?,?);");

        ps.setLong(1, value.getSkuId());
        ps.setLong(2, value.getUserId());
        ps.setString(3, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        ps.executeUpdate(); //结果集
        log.info("insert item:[{}]", JSON.toJSONString(value));
    }
}
