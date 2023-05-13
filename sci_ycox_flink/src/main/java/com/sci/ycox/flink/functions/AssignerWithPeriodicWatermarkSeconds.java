package com.sci.ycox.flink.functions;

import com.sci.ycox.flink.bean.SourceEntity;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * 定时提取水位
 * @Description
 * @Copyright Copyright (c) 2019
 * @Company: 
 * @author Xyb
 * @Date 2019年7月3日 下午5:43:53 
 *
 */
public class AssignerWithPeriodicWatermarkSeconds extends BoundedOutOfOrdernessTimestampExtractor<SourceEntity> {
	/**
	*
	*/
	private static final long serialVersionUID = -1211076389198922506L;

	public AssignerWithPeriodicWatermarkSeconds(int seconds) {
		super(Time.seconds(seconds));
	}

	@Override
	public long extractTimestamp(SourceEntity s) {
		return s.getRandom().getTime();
	}

}
