package com.sci.ycox.flink

import java.sql.Timestamp
import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.table.api.scala.StreamTableEnvironment

object TrackRealTimeAcquisitionHackAttack {
//  val properties = new Properties()
//
//  def init():Unit={
//    properties.setProperty("bootstrap.servers", "182.175.240.169:9092")
//    properties.setProperty("group.id", "flink-scala-test")
//    properties.setProperty("auto.offset.reset","latest")
//  }
//
//
//  private val extractor = new AscendingTimestampExtractor[Tuple11[Long,String,String,String,String,String,String,String,String,Long,String]]() {
//    override def extractAscendingTimestamp(element: Tuple11[Long,String,String,String,String,String,String,String,String,Long,String]): Long = element._10
//  }
//
//  def main(args: Array[String]): Unit = {
//    init()
//    // get the execution environment
//    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
//    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
//    env.setParallelism(1)
//    val tEnv = StreamTableEnvironment.create(env)
//    val kafkasource = new FlinkKafkaConsumer[String]("track-realTime-acquisition", new SimpleStringSchema(), properties)
//
//    val stream = env.addSource(kafkasource)
//
//    val structstream = stream.map { recode =>
//      val rtt = new RealTimeTrackerBean()
//      rtt.parse(recode)
//    }.map{r =>
//      val appid=try {
//        r.appid.toLong
//      } catch {
//        case _ => -1L
//      }
//
//      Tuple11(appid, r.platformid, r.search, r.coordinate, r.devicetype, r.platform, r.userid, r.ip, r.servertime, r.fronttime, r.guid)
//    }
//      .filter(_._10 > 0L ).filter(_._1 > 0L)
//
//
//    val markstream = structstream
//      .setParallelism(1)
//      .assignTimestampsAndWatermarks(extractor)
//    //.assignTimestampsAndWatermarks(new BoundedGenerator())
//    tEnv.registerDataStream("trackerTable", markstream, 'appid, 'platformid, 'search, 'coordinate, 'devicetype, 'platform, 'userid, 'ip, 'servertime, 'fronttime.rowtime, 'guid)
//    //    val maxIpStream1min=tEnv.sqlQuery("SELECT  count(ip) FROM " +
//    //     "trackerTable GROUP BY TUMBLE(fronttime, INTERVAL '20' SECOND) ")
//    //val maxIpStream1min = tEnv.sqlQuery("SELECT appid,fronttime from  trackerTable ")
//    val maxIpStream1min = tEnv.sqlQuery("SELECT count(appid), TUMBLE_END(fronttime, INTERVAL '30' SECOND) from  trackerTable GROUP BY TUMBLE(fronttime, INTERVAL '30' SECOND),appid")
//    //println(tEnv.explain(maxIpStream1min))
//    maxIpStream1min.toAppendStream[maxip].print()
//    env.execute("TrackRealTimeAcquisitionHackAttack")
//  }
//  case class maxip(ip: Long, time: Timestamp)
//  case class gip(ip: Long, p: String)
//  case class platformid(platformid: String)
//  case class wxs(ip: String, nettype: String, devicetype: String, coordinate: String, platform: String, fronttime: Long)
//  case class data(appid: String, platformid: String, search: String, coordinate: String, devicetype: String, platform: String, userid: String, ip: String, servertime: String, fronttime: Timestamp, guid: String
//                 )

}
