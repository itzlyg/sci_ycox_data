package com.sci.ycox.flink.operator;

import com.sci.ycox.flink.bean.SourceEntity;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;

public class AssignerWithPeriodicWatermarkSeconds extends BoundedOutOfOrdernessTimestampExtractor<SourceEntity> {
    /**
     *
     */
    private static final long serialVersionUID = -1211076389198922506L;
    public AssignerWithPeriodicWatermarkSeconds(int seconds){
        super(Time.seconds(seconds));
    }
    @Override
    public long extractTimestamp(SourceEntity element) {
        return element.getRandom();
    }
}
