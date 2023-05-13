package com.sci.ycox.kafka.producer;

import com.sci.ycox.kafka.bean.SourceEntity;
import com.sci.ycox.kafka.bean.SourceOut;
import com.sci.ycox.kafka.util.JsonUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.io.File;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@EnableScheduling
public class MsgSend {

    private static final Logger LOG = LoggerFactory.getLogger(MsgSend.class);

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.producer.topic}")
    private String topic;

//    @Scheduled(cron = "0/10 * * * * ng?")
    public void msg (){
        File file = new File("D:\\flinkJson.txt");
        List<String> msgs;
        SourceOut out;
        SourceEntity en;
        long t;
        String json;
        String msg;
        boolean read = false;
        int benginIndex = 0;
        ListenableFuture<SendResult<String, String>> future;
        try {
            msgs = FileUtils.readLines(file, Charset.defaultCharset());
            int j = msgs.size();
            for (int i = 0; i < j; i ++) {
                msg = msgs.get(i).trim();
                if (i == 0) {
                    String[] ss = StringUtils.split(msg, "-");
                    if ("0".equals(ss[0]) || StringUtils.isBlank(topic)) {
                        break;
                    }
                    benginIndex = Integer.parseInt(ss[1]);
                } else {
                    read = true;
                    if (StringUtils.isBlank(msg) || i < benginIndex) {
                        continue;
                    }
                    out = JsonUtil.toPojo(msg, SourceOut.class);
                    en = out.getArgs();
                    // 时间为空
                    if (StringUtils.isNotBlank(en.getExt())) {
                        t = epochMilli(en.getExt());
                        en.setExt(null);
                    } else {
                        t = System.currentTimeMillis();
                    }
                    en.setRandom(t);
                    out.setArgs(en);
                    json = JsonUtil.toJson(out);
                    System.out.println(json);
                    future = kafkaTemplate.send(topic, json);
                    future.addCallback(
                            s ->  {},
                            fail -> LOG.error("KafkaMessageProducer 发送消息失败！{}", fail.getMessage())
                    );
                }
            }
            // 改写状态
            msgs.set(0, "0-" + j);
            if (read) {
                FileUtils.writeLines(file, msgs);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static long epochMilli(String time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");
        LocalDateTime dateTime = LocalDateTime.parse(time, formatter);
        return dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }
}
