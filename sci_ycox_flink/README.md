##### 数据接收与传递
- 使用元祖方式    
    定义元祖的数量、类型，与表的注册、sql字段类型数量要一一对应。
- 使用JavaBean方式
    > 传入的JavaBean
    
    ```
    public class AppSource {
    
    	private String appId;
    
    	private String pvi;
    
    	private String ip;
    
    	private String ty;
    
    	private String url;
    
    	private String lg;
    
    	private String tz;
    
    	private String version;
    
    	private long ptime;
    
    	private Timestamp ttime;
    	
    	
    	.......
    
    ```
    
    > 注册Table
    ```
    tableEnv.registerDataStream("app_source_time", beans, "appId,pvi,ip,url,ptime,proctime.proctime");
    ```
    **appId ...等对应Bean中的属性 proctime.proctime 为注册表自有的属性**
    
    
   
   >接收的JavaBean
    
   ```
    public class ResultInfo {
        private String appId;
        private String pvi;
        private String name;
        private Timestamp ptime;
        private Long pvcount;
        
        ......
   ```

   > 对应的SQL语句
   
   ```
   sql.append(" SELECT appId, '' name, pvi,  TUMBLE_END(proctime, INTERVAL '60' SECOND) as ptime, count(pvi) as pvcount  ");
   ......
   DataStream<ResultInfo> strea = tableEnv.toAppendStream(query, ResultInfo.class);
   ......
   ```   
   
   **SQL语句中的字段别名必须与Bean中的对应（大小写、数量）**     
    
    




#### 比较
- toAppendStream和toRetractStream  
    两个方法用来将sql处理的数据转化为流时候。
    - toAppendStream 追加模式   
        只有在动态Table仅通过INSERT更改修改时才能使用此模式，即它仅附加。在数据不断增加的时候可以使用追加模式。
    - toRetractStream 缩进模式，始终可以使用
        - 返回Tuple2<Boolean, T> 
        - Boolean 它用true或false来标记数据的插入和撤回，返回true代表数据插入，false代表数据的撤回
        - T:查询到的数据，并映射到 T对象
    
- proctime和rowtime  
    在将stream注册成表的时候可以拿到proctime.proctime和rowtime.rowtime
    - proctime Processing Time
    
- 窗口组
    - TUMBLE(time_attr, interval) 滚动时间窗口  
        
    - HOP(time_attr, interval, interval) 滑动时间窗口     
        比如每隔五分钟的用户活跃度
    - SESSION(time_attr, interval) 会话时间窗口  
    
- 水位线的设置
    - 有两种模式 事件提取模式和时间模式
    - 事件提取模式：每来一个事件即触发  
        ```
         /**
         * 事件提取
         */
        public static class AssignerWithPunctuatedWatermarksExtractor implements AssignerWithPunctuatedWatermarks<AppSource> {
            /**
             * 
             */
            private static final long serialVersionUID = -8545223326666410866L;
    
            /** 延迟时间 */
            private final long maxOutOfOrderness = 3500; // 3.5 seconds
    
            /** 最大事件时间 */
            private long currentMaxTimestamp;
    
            @Nullable
            @Override
            public Watermark checkAndGetNextWatermark(AppSource lastElement, long extractedTimestamp) {
                //return new Watermark(System.currentTimeMillis() - maxOutOfOrderness);
                return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
            }
    
            @Override
            public long extractTimestamp(AppSource element, long previousElementTimestamp) {
                // return element.f4;
                long timestamp = element.getPtime();
                currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
                return timestamp;
            }
        }
      ```   
      
   - 时间模式：固定的时间去触发
       ```
       public static class AssignerWithPeriodicWatermarksExtractor extends BoundedOutOfOrdernessTimestampExtractor<AppSource> {
       
        /** */
        private static final long serialVersionUID = -3211076389198922505L;
        public AssignerWithPeriodicWatermarksExtractor(){
               super(Time.seconds(10));
           }
           @Override
           public long extractTimestamp(AppSource element) {
               return element.getPtime();
           }
   
       }
       
       ```

<br/><br/><br/>
***

#### RedisSink UV、PV、IP、VV的存储结构
- 实时数据
    - key值
        app_id:yyyy-MM-dd:type 如：<font color=#FF0000>APP_A0001:2019-07-08:IP</font> 
    - value
        存储的是HyperLogLog类型 使用命令“PFCOUNT key” 获取总数量。实时增量，用本次请求的数据减去上一次请求的数据
        
- 按小时窗口统计
    - key
       app_id:yyyy-MM-dd-HH:type 如：APP_A0001:2019-07-08-13:IP
    - value
       存储的即为写这一个小时内的数据值，使用命令“get key”获取     


***
<br/><br/><br/>

##### Redis与RedisCommand数据类型对比


  Data Type|Redis Command[Sink]|说明    
  :-:|:-:|-     
  HASH|HSET|无序散列
  LIST|RPUSH、LPUSH|链表
  SET|SADD|无序字集
  PUBSUB|PUBLISH|消息订阅
  STRING|SET|字符串
  HYPER_LOG_LOG|PFADD|位图
  SORTED_SET|ZADD|每一个成员都会有一个<br>分数(score)与之关联
  SORTED_SET|ZREM|每一个成员都会有一个<br>分数(score)与之关联
  
  
   
        
#### Reduce
- DataSet
```   
    ReduceOperator extends SingleInputUdfOperator<IN, IN, ReduceOperator<IN>>
        SingleInputUdfOperator extends SingleInputOperator<IN, OUT, O> implements UdfOperator
            SingleInputOperator extends Operator
                Operator extends DataSet
                    DataSet
            UdfOperator
```
- DataStream
```
    StreamGroupedReduce extends AbstractUdfStreamOperator implements OneInputStreamOperator
        AbstractUdfStreamOperator extends AbstractStreamOperator implements OutputTypeConfigurable
            AbstractStreamOperator implements StreamOperator<OUT>, Serializable
            OutputTypeConfigurable
        OneInputStreamOperator extends StreamOperator
            StreamOperator<OUT> extends CheckpointListener, KeyContext, Disposable, Serializable
```

#### Sink JDBC
##### JDBCAppendTableSink
##### JDBCOutputFormat
##### JDBCSinkFunction
##### 自定义Sink
