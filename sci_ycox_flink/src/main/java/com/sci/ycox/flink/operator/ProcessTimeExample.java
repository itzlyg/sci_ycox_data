package com.sci.ycox.flink.operator;

import com.alibaba.fastjson.JSONObject;
import com.sci.ycox.flink.bean.ResultCount;
import com.sci.ycox.flink.bean.ResultInfo;
import com.sci.ycox.flink.bean.SourceEntity;
import com.sci.ycox.flink.bean.SourceOut;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;

import java.util.Properties;

/**
 * 用户在固定时间(一分钟)内的点击量
 */
public class ProcessTimeExample {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000); // 非常关键，一定要设置启动检查点！！
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        // 创建注册表环境
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "127.0.0.1:8802");

        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>("flinktest", new SimpleStringSchema(), props);

        DataStream<String> stream = env.addSource(consumer);

        // appId uid ip url ptime 将数据转为bean的数据流
        DataStream<SourceEntity> beans = stream.map(json -> JSONObject.parseObject(json, SourceOut.class).getArgs());
        join(beans, env, tableEnv);

        env.execute("Flink-Kafka demo");
    }


    /**
     *
     * @param beans
     * @param tableEnv
     */
    private static void ordiy (DataStream<SourceEntity> beans, StreamTableEnvironment tableEnv){


        /**
         * 注册表
         * 参数：表名称、数据源、【字段名】
         * sql语句大小写敏感
         * proctime.proctime 和 rowtime.rowtime 为注册表默认的属性
         * proctime 对应 process time
         * rowtime 对应 event time 例如：ptime.rowtime ptime为数据源中的时间戳
         */
        tableEnv.registerDataStream("app_source_time", beans, "appId,pvi,ip,url,ptime,proctime.proctime");
        StringBuffer sql = new StringBuffer("");
        sql.append(" SELECT appId, '' name, pvi,  TUMBLE_END(proctime, INTERVAL '60' SECOND) as ptime, count(pvi) as pvcount  ");
        sql.append("FROM  app_source_time  ");
        //sql.append("WHERE appId = 'APP_A0001' ");
        sql.append("GROUP BY TUMBLE(proctime, INTERVAL '60' SECOND),appId, pvi ");
        Table query = tableEnv.sqlQuery(sql.toString());
        /**
         * 使用对象接收表数据的时候，字段数量要与bean的一样 大小写一样，顺序可以不一致
         *
         */
        DataStream<ResultInfo> result = tableEnv.toAppendStream(query, ResultInfo.class);
        result.map(v -> v.toString()).print();
    }

    /**
     * 进行多表关联的查询
     * 注册一个固定数据的表 一个process time窗口的表
     * @param beans
     * @param env
     * @param tableEnv
     */
    public static void join (DataStream<SourceEntity> beans, StreamExecutionEnvironment env, StreamTableEnvironment tableEnv) {
        beans.keyBy(t -> t.getR2());
        // 注册数据源的表
        tableEnv.registerDataStream("app_source_time", beans, "appId,pvi,ip,url,ptime,proctime.proctime");
        Tuple2<String, String>[] ts = new Tuple2[2];
        ts[0] = Tuple2.of("user_id_a_001", "用户a1[存在的]");
        ts[1] = Tuple2.of("user_id_1_001", "用户[不存在的]");
        DataStream<Tuple2<String, String>> data1 = env.fromElements(ts);
        // 注册一张固定数据的表
        tableEnv.registerDataStream("user_info", data1, "user_id,user_name ");
        StringBuffer sql = new StringBuffer("");
        sql.append("SELECT S.appId,S.pvi,S.pvcount, U.user_name name, S.ptime  FROM (SELECT TUMBLE_END(proctime, INTERVAL '60' SECOND) as ptime, appId,  pvi,count(pvi) as pvcount ");
        sql.append("FROM  app_source_time  ");
        sql.append("WHERE appId = 'APP_A0001' ");
        sql.append("GROUP BY TUMBLE(proctime, INTERVAL '60' SECOND),appId, pvi) S ");
        sql.append("LEFT JOIN user_info U ON S.pvi = U.user_id ");
        Table query = tableEnv.sqlQuery(sql.toString());
        /**
         * 数据读取 ，使用元祖Tuple2<Boolean, ResultInfo>接收
         * Boolean 它用true或false来标记数据的插入和撤回，返回true代表数据插入，false代表数据的撤回
         * ResultInfo  查询到的数据，并映射到 ResultInfo对象
         *
         */
        DataStream<Tuple2<Boolean, ResultInfo>> res = tableEnv.toRetractStream(query, ResultInfo.class);
        res.map(t -> t.f1.toString()).print();
    }

    /**
     * 根据appId计数
     * 使用bean对象传入和接收
     * @param beans
     * @param env
     * @param tableEnv
     */
    public static void count (DataStream<SourceEntity> beans, StreamExecutionEnvironment env, StreamTableEnvironment tableEnv) {
        Table table = tableEnv.fromDataStream(beans);
        tableEnv.registerTable("app_source", table);
        Table query = tableEnv.sqlQuery("select appId name ,count(appId) counts from app_source group by appId ");
        DataStream<Tuple2<Boolean, ResultCount>> result = tableEnv.toRetractStream(query, ResultCount.class);
        result.map(v -> v.f1.toString()).print();
    }

}
