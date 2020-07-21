package com.vanas.realtime.app

import java.util

import com.alibaba.fastjson.JSON
import com.vanas.common.Constant
import com.vanas.realtime.bean.{AlertInfo, EventLog}
import com.vanas.realtime.util.{ESUtil, MyKafkaUtil}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Minutes, Seconds, StreamingContext}

import scala.util.control.Breaks
import ESUtil._

/**
 * @author Vanas
 * @create 2020-07-18 8:34 上午 
 */
object AlertApp extends BaseApp {
    override def run(ssc: StreamingContext): Unit = {
        val eventLogStream: DStream[EventLog] = MyKafkaUtil.getKafkaStream(ssc, Constant.EVENT_TOPIC)
                .map(json => JSON.parseObject(json, classOf[EventLog]))
                //reduceByKeyAndWindow
                //直接在流上使用window，将来所有的操作都是基于这个窗口的
                .window(Minutes(5), Seconds(6)) //给流添加窗口
        //按照设备id进行分组
        val eventLogGroupedStream: DStream[(String, Iterable[EventLog])] = eventLogStream
                .map(event => (event.mid, event))
                .groupByKey
        //2.产生预警信息
        val alterInfoStream: DStream[(Boolean, AlertInfo)] = eventLogGroupedStream.map {
            //eventLogIt表示当前mid上5分钟内所有的事件记录
            case (mid, eventLogIt) =>
                //领取优惠券的用户
                val uidSet = new util.HashSet[String]()
                //存储5分钟内当前设备上所有的事件
                val evenListt = new util.ArrayList[String]()
                //存储优惠券对应的那些商品id
                val itemSet = new util.HashSet[String]()
                //表示是否点击过商品
                var isClickItem = false

                Breaks.breakable {
                    eventLogIt.foreach(log => {
                        //把事件id添加到eventList
                        evenListt.add(log.eventId)
                        //只关注领取优惠券的用户
                        log.eventId match {
                            case "coupon" =>
                                uidSet.add(log.uid)
                                itemSet.add(log.itemId) //优惠券对应的商品
                            case "clickItem" =>
                                //一旦出现浏览商品，则不会再产生预警信息
                                //匿名函数里不能使用return
                                isClickItem = true
                                Breaks.break
                            case _ => //其他事件不做任何处理
                        }
                    })
                }
                //(是否预警，alert。。。）
                (!isClickItem && uidSet.size() > 3, AlertInfo(mid, uidSet, itemSet, evenListt, System.currentTimeMillis()))
        }
        alterInfoStream.print(10000)

        //  3. 把数据写入到es
        alterInfoStream
                .filter(_._1)
                .map(_._2)
                .foreachRDD(rdd => {
                    //                    rdd.foreachPartition(it => {
                    //                        //es 每个document都有id，如果id使用分钟数表示：
                    //                        //id重复后面的会覆盖前面的
                    //                        ESUtil.insertBulk("gmall_coupon_alert", it.map(info => (info.mid + ":" + info.ts / 1000 / 60, info)))
                    rdd.saveToES("gmall_coupon_alert")
                })
    }
}

/*
需求：同一设备，5分钟内三次及以上用不同账号登录并领取优惠劵，
     并且在登录到领劵过程中没有浏览商品。同时达到以上要求则产生一条预警日志。 同一设备，每分钟只记录一次预警。

     同一设备->group by mid_id
     5分钟内的数据，每6秒统计一次->窗口 窗口的长度5分钟 窗口的步长6秒
     三次及以上不同账号登入->统计每个设备的登陆的用户数
     领取优惠券->领取优惠券的行为

     并且在登录到领劵过程中没有浏览商品->事件中没有浏览商品的行为
     同一设备，每分钟只记录一次预警 ->不在spark-streaming完成 让es来完成

 */
