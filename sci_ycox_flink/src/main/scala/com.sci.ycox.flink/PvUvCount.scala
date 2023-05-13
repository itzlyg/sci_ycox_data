package com.sci.ycox.flink

import java.sql.Timestamp
import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.table.api.scala.StreamTableEnvironment

/**
  * https://blog.csdn.net/ddxygq/article/details/88727649
  */
object TrackRealTimeAcquisitionHackAttack2 {
//  def main(args: Array[String]): Unit = {
//    val env = StreamExecutionEnvironment.getExecutionEnvironment
//
//    // 容错
//    env.enableCheckpointing(5000)
//    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
//    env.setStateBackend(new FsStateBackend("file:///D:/space/IJ/bigdata/src/main/scala/com/ddxygq/bigdata/flink/checkpoint/flink/tagApp"))
//
//    // kafka 配置
//    val ZOOKEEPER_HOST = "qcloud-test-hadoop01:2181,qcloud-test-hadoop02:2181,qcloud-test-hadoop03:2181"
//    val KAFKA_BROKERS = "qcloud-test-hadoop01:9092,qcloud-test-hadoop02:9092,qcloud-test-hadoop03:9092"
//    val TRANSACTION_GROUP = "flink-helper-label-count"
//    val TOPIC_NAME = "tongji-flash-hm2-helper"
//    val kafkaProps = new Properties()
//    kafkaProps.setProperty("zookeeper.connect", ZOOKEEPER_HOST)
//    kafkaProps.setProperty("bootstrap.servers", KAFKA_BROKERS)
//    kafkaProps.setProperty("group.id", TRANSACTION_GROUP)
//
//    // watrmark 允许数据延迟时间
//    val MaxOutOfOrderness = 86400 * 1000L
//
//    // 消费kafka数据
//    val streamData: DataStream[(String, String, String)] = env.addSource(
//      new FlinkKafkaConsumer010[String](TOPIC_NAME, new SimpleStringSchema(), kafkaProps)
//    ).assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor[String](Time.milliseconds(MaxOutOfOrderness)) {
//      override def extractTimestamp(element: String): Long = {
//        val t = JSON.parseObject(element)
//        val time = JSON.parseObject(JSON.parseObject(t.getString("message")).getString("decrypted_data")).getString("time")
//        time.toLong
//      }
//    }).map(x => {
//      var date = "error"
//      var guid = "error"
//      var helperversion = "error"
//      try {
//        val messageJsonObject = JSON.parseObject(JSON.parseObject(x).getString("message"))
//        val datetime = messageJsonObject.getString("time")
//        date = datetime.split(" ")(0)
//        // hour = datetime.split(" ")(1).substring(0, 2)
//        val decrypted_data_string = messageJsonObject.getString("decrypted_data")
//        if (!"".equals(decrypted_data_string)) {
//          val decrypted_data = JSON.parseObject(decrypted_data_string)
//          guid = decrypted_data.getString("guid").trim
//          helperversion = decrypted_data.getString("helperversion")
//        }
//      } catch {
//        case e: Exception => {
//          println(e)
//        }
//      }
//      (date, helperversion, guid)
//    })
//    // 这上面是设置watermark并解析json部分
//    // 聚合窗口中的数据，可以研究下applyWith这个方法和OnWindowedStream这个类
//    val resultStream = streamData.keyBy(x => {
//      x._1 + x._2
//    }).timeWindow(Time.days(1))
//      .trigger(ContinuousProcessingTimeTrigger.of(Time.seconds(1)))
//      .applyWith(("", List.empty[Int], Set.empty[Int], 0L, 0L))(
//        foldFunction = {
//          case ((_, list, set, _, 0), item) => {
//            val date = item._1
//            val helperversion = item._2
//            val guid = item._3
//            (date + "_" + helperversion, guid.hashCode +: list, set + guid.hashCode, 0L, 0L)
//          }
//        }
//        , windowFunction = {
//          case (key, window, result) => {
//            result.map {
//              case (leixing, list, set, _, _) => {
//                (leixing, list.size, set.size, window.getStart, window.getEnd)
//              }
//            }
//          }
//        }
//      ).keyBy(0)
//      .flatMapWithState[(String, Int, Int, Long, Long),(Int, Int)]{
//      case ((key, numpv, numuv, begin, end), curr) =>
//
//        curr match {
//          case Some(numCurr) if numCurr == (numuv, numpv) =>
//            (Seq.empty, Some((numuv, numpv))) //如果之前已经有相同的数据,则返回空结果
//          case _ =>
//            (Seq((key, numpv, numuv, begin, end)), Some((numuv, numpv)))
//        }
//    }
//
//    // 最终结果
//    val resultedStream = resultStream.map(x => {
//      val keys = x._1.split("_")
//      val date = keys(0)
//      val helperversion = keys(1)
//      (date, helperversion, x._2, x._3)
//    })
//
//    resultedStream.print()
//    env.execute("PvUvCount")
//
//  }

}
