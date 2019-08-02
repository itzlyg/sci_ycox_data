package com.sci.ycox.springflink.sink;

import com.sci.ycox.springflink.bean.AppDataSources;
import com.sci.ycox.springflink.dao.AppDataSourcesMapper;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * @Description spring boot mybatis flink
 * @Author xyb
 * @Date 2019-07-23 10:39:47
 * @Version 1.0.0
 */
@EnableAutoConfiguration
@MapperScan(basePackages = {"com.sci.ycox.**.dao"})
public class SimpleSink extends RichSinkFunction<Tuple3<String, String, Integer>> {

    private AppDataSourcesMapper mapper;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        SpringApplication application = new SpringApplication();
        application.setBannerMode(Banner.Mode.OFF);

        ApplicationContext context = null;
        try {
            context = application.run(new String[]{});
            mapper = context.getBean(AppDataSourcesMapper.class);
            if (mapper == null) {
                System.out.println("545");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }


    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void invoke(Tuple3<String, String, Integer> value, Context context) throws Exception {
        System.out.println("run invoke");
        List<AppDataSources> list = mapper.list();
        if (list == null) {
            System.out.println(list.size() + "--------------");
        } else {
            System.out.println("null -------");
        }
    }
}
