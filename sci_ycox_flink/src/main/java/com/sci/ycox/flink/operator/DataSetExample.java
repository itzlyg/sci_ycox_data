package com.sci.ycox.flink.operator;

import com.sci.ycox.flink.bean.dataSet.Student;
import com.sci.ycox.flink.bean.dataSet.Wc;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.functions.FunctionAnnotation;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.util.Collector;

/**
 * https://blog.csdn.net/lxhandlbb/article/details/78671464
 */
public class DataSetExample {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        env.enableCheckpointing(5000);
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        reduceGroups(env);
        env.execute("job name");
//        forwardedFields();
//        forwardedFieldsStream();
    }


    private static void reduceGroups (StreamExecutionEnvironment env){
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        DataStreamSource<Tuple2<String, Integer>> ips = env.fromElements(
                Tuple2.of("u_1",  1),
                Tuple2.of("u_2",  1),
                Tuple2.of("u_2",  1),
                Tuple2.of("u_3",  1),
                Tuple2.of("u_3",  1),
                Tuple2.of("u_3",  1),
                Tuple2.of("u_4",  1),
                Tuple2.of("u_5",  1)

        );
        ips.keyBy(0).reduce((m, n) -> Tuple2.of(m.f0, m.f1 + n.f1))
                .map(o ->"------------" + o.toString())
                .print();
        System.out.println("---------------------------------------------------------------");
        ips.keyBy(0).reduce((m, n) -> Tuple2.of(m.f0, m.f1 + n.f1))
                .countWindowAll(2)
//                .sum(1)

                .reduce((m, n) -> Tuple2.of(m.f0,  m.f1))

                .print();

        System.out.println("---------------------------------------------------------------");

//        List<String> list = new ArrayList<>();
//        list.add("5655");
//
//        ExecutionEnvironment ee = ExecutionEnvironment.getExecutionEnvironment();
//
//        DataSet<String> ds = ee.fromElements("12");
//        ds.
//        ips.keyBy(0).re

    }


    private static void reduceGroup (StreamExecutionEnvironment env){
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        DataStreamSource<Tuple3<String, String, Integer>> ips = env.fromElements(
                Tuple3.of("u_1", "sid_1", 1),
                Tuple3.of("u_1", "sid_2", 1),
                Tuple3.of("u_2", "sid_1", 1),
                Tuple3.of("u_2", "sid_2", 1),
                Tuple3.of("u_1", "sid_1", 1),
                Tuple3.of("u_1", "sid_3", 1),
                Tuple3.of("u_1", "sid_1", 1)
        );

        ips.keyBy(0).reduce((m, n) -> Tuple3.of(m.f0, m.f1, m.f2 + n.f2))
                .countWindowAll(5)
                .reduce((m, n) -> Tuple3.of(m.f0, m.f1, m.f2 + n.f2))

                .print();

        System.out.println("---------------------------------------------------------------");

//        List<String> list = new ArrayList<>();
//        list.add("5655");
//
//        ExecutionEnvironment ee = ExecutionEnvironment.getExecutionEnvironment();
//
//        DataSet<String> ds = ee.fromElements("12");
//        ds.
//        ips.keyBy(0).re

    }


    private static void vv (StreamExecutionEnvironment env){
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        DataStreamSource<Tuple3<String, String, Integer>> ips = env.fromElements(
                Tuple3.of("u_1", "sid_1", 1),
                Tuple3.of("u_1", "sid_2", 1),
                Tuple3.of("u_2", "sid_1", 1),
                Tuple3.of("u_2", "sid_2", 1),
                Tuple3.of("u_1", "sid_1", 1),
                Tuple3.of("u_1", "sid_3", 1),
                Tuple3.of("u_1", "sid_1", 1)
        );
        tableEnv.registerDataStream("vv_test", ips, "uid,sid");
        StringBuffer sql = new StringBuffer("");
        sql.append("SELECT uid, MIN(c) C from ( SELECT uid, count(sid) c FROM (");
        sql.append("SELECT uid,sid FROM vv_test GROUP BY uid,sid ");
        sql.append(") GROUP BY uid ) A GROUP BY uid ");
        Table query = tableEnv.sqlQuery(sql.toString());
        DataStream<Tuple2<Boolean, Tuple2<String, Long>>> stream = tableEnv.toRetractStream(query, Types.TUPLE(Types.STRING, Types.LONG));
        stream.filter(o->o.f0).print();

    }

    private static  void collector (StreamExecutionEnvironment env){
//通过字符串构建数据集
        // Who's there?", "I think I hear them. Stand, ho! Who's there?
        DataStreamSource<String> text = env.fromElements("a a b ab a b bb");
//分割字符串、按照key进行分组、统计相同的key个数

        SingleOutputStreamOperator<Tuple2<String, Integer>> wordCounts = text
                .flatMap(
                        new FlatMapFunction<String, Tuple2<String, Integer>>(){
                            @Override
                            public void flatMap(String line, Collector<Tuple2<String, Integer>> out) {
                                for (String word : line.split(" ")) {
                                    out.collect(new Tuple2<String, Integer>(word, 1));
                                }
                            }})
                .keyBy(0)
//                .countWindow(2)

//                .timeWindow(Time.minutes(1L))
//                .window(Time.seconds(1))
//                .reduce((v1,v2) ->Tuple2.of(v1.f0, v1.f1 + v2.f1))
                .reduce(new MayMap())
                .keyBy(0)
                .max(1)
                ;
        wordCounts.print();
//        wordCounts.print();
    }

    private static void forwardedFields () throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
//        env.
        DataSource<Tuple2<String, Double>> text = env.fromElements(
                Tuple2.of("a", 10.0),
                Tuple2.of("b", 20.0),
                Tuple2.of("a", 30.0)
        );
        text.groupBy(0).reduce((m,n)->Tuple2.of(m.f0, n.f1 + m.f1))
                .map(o -> "ExecutionEnvironment-[" + o.toString())
                .print();
    }

    private static void forwardedFieldsStream () throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000); // 非常关键，一定要设置启动检查点！！
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        DataStreamSource<Tuple2<String, Double>> text = env.fromElements(
                Tuple2.of("a", 10.0),
                Tuple2.of("b", 20.0),
                Tuple2.of("a", 30.0)
        );
        text.keyBy(0).reduce((m,n)->Tuple2.of(m.f0, n.f1 + m.f1))
                .map(o -> "StreamExecutionEnvironment-[" + o.toString())
                .print();
        env.execute("Flink-Kafka demo");
    }

    private static void excute () throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000); // 非常关键，一定要设置启动检查点！！
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        env.execute("Flink-Kafka demo");
    }






//    @FunctionAnnotation.ForwardedFields("0")
//    @FunctionAnnotation.ForwardedFields("0->1; 1->0")
    @FunctionAnnotation.ForwardedFieldsFirst("0; 1")
// The third field from the input tuple is copied to the third position of the output tuple
    @FunctionAnnotation.ForwardedFieldsSecond("2")
    private static class MayMap implements ReduceFunction<Tuple2<String, Integer>> {
        @Override
        public Tuple2<String, Integer> reduce(Tuple2<String, Integer> v1, Tuple2<String, Integer> v2) throws Exception {
            return Tuple2.of(v1.f0, v1.f1 + v2.f1);
        }
    }




    private static void vv1 (StreamExecutionEnvironment env){
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        DataStreamSource<Tuple3<String, String, Integer>> ips = env.fromElements(
                Tuple3.of("u_1", "sid_1", 1),
                Tuple3.of("u_1", "sid_2", 1),
                Tuple3.of("u_2", "sid_1", 1),
                Tuple3.of("u_2", "sid_2", 1),
                Tuple3.of("u_1", "sid_1", 1),
                Tuple3.of("u_1", "sid_3", 1),
                Tuple3.of("u_1", "sid_1", 1)
        );
//        tableEnv.registerDataStream("vv_test", ips, "uid,sid");
//        StringBuffer sql = new StringBuffer("");
//        sql.append("SELECT uid, count(sid) c FROM (");
//
//        sql.append("SELECT uid,sid FROM vv_test GROUP BY uid,sid ");
//        sql.append(") GROUP BY uid ");

        ips.keyBy(0, 1)
//           .maxBy(2)
                .reduce((o1, o2) -> Tuple3.of(o1.f0, o1.f1, o1.f2 + o2.f2))



//
//                .keyBy(0)
//                .reduce((o1, o2) -> Tuple3.of(o1.f0, "", o1.f2 + o2.f2 ))
                .print();



//        Table query = tableEnv.sqlQuery(sql.toString());
//        DataStream<Tuple2<Boolean, Tuple2<String, Long>>> stream = tableEnv.toRetractStream(query, Types.TUPLE(Types.STRING, Types.LONG));
//        stream.print();
//        ips.map(o -> Tuple2.of(o.f0 + o.f1, o.f2))
//                .keyBy(o -> o.f1)
//                .reduce((o1, o2) -> Tuple2.of(o1.f0, o1.f1 + o2.f1))
//
//
////                keyBy(0, 1)
////                .reduce((o1, o2) -> Tuple3.of( o1.f0, o1.f1, o1.f2 +o2.f2 ))
//                .print();

    }


    private static void uv (StreamExecutionEnvironment env){
        DataStreamSource<Tuple2<String, String>> ips = env.fromElements(
                Tuple2.of("127.0.0.1", "12"),
                Tuple2.of("127.0.0.2", "12234"),
                Tuple2.of("127.0.0.3", "1223"),
                Tuple2.of("127.0.0.1", "122342"),
                Tuple2.of("127.0.0.2", "12yu")
        );
        ips.keyBy(1).print();
        FlinkJedisPoolConfig redisConfig = new FlinkJedisPoolConfig.Builder().setHost("127.0.0.1").setDatabase(15).build();
        ips.addSink(new RedisSink<Tuple2<String, String>>(redisConfig, new DataSetExample.RedisSinkExample()));

    }

    public static class RedisSinkExample implements RedisMapper<Tuple2<String, String>> {

        @Override
        public RedisCommandDescription getCommandDescription() {
            /** LPUSH 最新的数据在上面 */
            return new RedisCommandDescription(RedisCommand.PFADD, null);
        }

        @Override
        public String getKeyFromData(Tuple2<String, String> info) {
            return "hyperloglog";
        }

        @Override
        public String getValueFromData(Tuple2<String, String> info) {
            return info.f0;
        }
    }


    private static void upsideDown (StreamExecutionEnvironment env) {
        DataStreamSource<Wc> data = env.fromElements(
                new Wc("LISI", 1),
                new Wc("LISI", 1),
                new Wc("LISI", 2),
                new Wc("LISI", 4),
                new Wc("LISI", 3),
                new Wc("LISI", 6)
                //1, 1, 2, 5, 4
        );
        //Tuple2.apply()
        //objsource.map(obj -> Tuple2.of(((UserScanJ) obj).getIp(), 1l)).keyBy(t -> ((Tuple2) t).f0).reduce((v1, v2) -> Tuple2.of(v2, v1));
        data.map(r -> Tuple2.of(r.getWord(), r.getSalary()))
                .keyBy(r -> r.f0)
                .reduce((v1, v2) -> Tuple2.of(v2.f0, v1.f1))
        .print();
    }

    public static void groupBy (StreamExecutionEnvironment env){
        DataStreamSource<Wc> data = env.fromElements(new Wc("LISI", 600),
                new Wc("LISI", 400),
                new Wc("WANGWU", 300),
                new Wc("ZHAOLIU", 700)
                );
        data.keyBy(t -> t.getWord())
            .reduce((w1, w2) -> new Wc(w1.getWord(), w1.getSalary() + w2.getSalary()))

            .print();
    }

    public static void groupTest2 (StreamExecutionEnvironment env){
        DataStreamSource<Student> data = env.fromElements(
                new Student("lisi", "shandong", 2400.00),
                new Student("zhangsan", "henan", 2600.00),
                new Student("lisi", "shandong", 2700.00),
                new Student("lisi", "guangdong", 2800.00)
        );
        // new String[]{v.getName(), v.getAddr()}
        data.keyBy(v -> new String[]{v.getName(), v.getAddr()})
                .reduce((v1, v2) -> new Student(v1.getName() + "-" + v2.getName(),
                        v1.getAddr() + "-" + v2.getAddr(), v1.getSalary() + v2.getSalary()))
                .print();
    }

    public static void reduce (StreamExecutionEnvironment env){
        DataStreamSource<Object[]> data = env.fromElements(
                new Object[]{20, "zhangsan"},
                new Object[]{22, "zhangsan"},
                new Object[]{22, "lisi"},
                new Object[]{20, "zhangsan"}
        );
        data.keyBy(v -> {
            Object[] os = v;
            return  (String)os[1];
        })
        ;
    }

}

