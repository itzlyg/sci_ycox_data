package com.sci.ycox.kafka.producer;

import com.sci.ycox.kafka.bean.SourceEntity;
import com.sci.ycox.kafka.bean.SourceOut;
import com.sci.ycox.kafka.util.JsonUtil;
import com.sci.ycox.kafka.util.RandomSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;

@Component
@EnableScheduling
public class KafkaMessageProducer {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(KafkaMessageProducer.class);

	@Resource
	private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.producer.topic}")
    private String topic;


	@Scheduled(cron = "0/10 * * * * ?")
    //@Scheduled(cron = "* 15 * * * ?")
	public void send() {
		int i = RandomSource.random(1000);
		String message;
		ListenableFuture<SendResult<String, String>> future = null;
		SourceEntity app;
        SourceOut out = new SourceOut();
		for (int j = 0; j < i; j++) {
            app = new SourceEntity();
            app.create();
            out.setArgs(app);
			message = JsonUtil.toJson(out);
			LOG.info("topic=" + topic + ",message=" + message);
			future = kafkaTemplate.send(topic, message);
			/** lambda表达式只支持函数式（只有一个抽象方法）接口 */
			future.addCallback(
					s ->  {},
                    fail -> LOG.error("KafkaMessageProducer 发送消息失败！{}", fail.getMessage())
			);
		}
		
	}

}
