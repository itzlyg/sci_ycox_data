package com.sci.ycox.flink.functions;

import com.sci.ycox.flink.bean.SinkResult;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Map;


/**
 * @Description 去除reduce过程中的中间过程
 * @Author xyb
 * @Date 2019-07-26 10:17:20
 * @Version 1.0.0
 */
public class RemoveReduceProcess {

    /**
     * @Description 处理reduce中间计算结果
     * @Date 2019-07-26 10:18:20
     * @param stream
     * @return 结果流
     */
    public SingleOutputStreamOperator<SinkResult> excute(DataStream<Tuple2<Boolean, SinkResult>> stream) {
        SingleOutputStreamOperator<SinkResult> soso = stream.filter(t -> t.f0)
                .process(new ProcessFunction<Tuple2<Boolean, SinkResult>, SinkResult>() {
                    private static final long serialVersionUID = -5290409009646407490L;

                    @Override
                    public void processElement(Tuple2<Boolean, SinkResult> value, Context ctx, Collector<SinkResult> out) throws Exception {
                        out.collect(value.f1);
                    }
                })
                .timeWindowAll(Time.hours(1))
                .aggregate(new AggregateFunction<SinkResult, HashMap<String, SinkResult>, HashMap<String, SinkResult>>() {

                    private static final long serialVersionUID = -8361217950335080847L;

                    @Override
                    public HashMap<String, SinkResult> createAccumulator() {
                        return new HashMap<>(16);
                    }

                    @Override
                    public HashMap<String, SinkResult> add(SinkResult v, HashMap<String, SinkResult> accumulator) {
                        SinkResult sr = accumulator.get(v.getAppid());
                        boolean ifn = sr == null || v.getNum() > sr.getNum();
                        if (ifn) {
                            accumulator.put(v.getAppid(), v);
                        }
                        return accumulator;
                    }

                    @Override
                    public HashMap<String, SinkResult> getResult(HashMap<String, SinkResult> accumulator) {
                        return accumulator;
                    }

                    @Override
                    public HashMap<String, SinkResult> merge(HashMap<String, SinkResult> a, HashMap<String, SinkResult> b) {
                        return null;
                    }
                }).process(new ProcessFunction<HashMap<String, SinkResult>, SinkResult>() {
                	
                    private static final long serialVersionUID = 1480622036211644865L;

                    @Override
                    public void processElement(HashMap<String, SinkResult> v, Context ctx, Collector<SinkResult> out) throws Exception {
                        for (Map.Entry<String, SinkResult> e : v.entrySet()) {
                            out.collect(e.getValue());
                        }
                    }
                });
        return soso;

    }

}
