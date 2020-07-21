package com.vanas.realtime.app

import com.alibaba.fastjson.JSON
import com.vanas.common.Constant
import com.vanas.realtime.bean.OrderInfo
import com.vanas.realtime.util.MyKafkaUtil
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream

/**
 * @author Vanas
 * @create 2020-07-17 2:14 下午 
 */
object OrderApp extends BaseApp {
    override def run(ssc: StreamingContext): Unit = {
        val orderInfoStream: DStream[OrderInfo] = MyKafkaUtil.getKafkaStream(ssc, Constant.TOPIC_ORDER_INFO)
                .map(json => {
                    JSON.parseObject(json, classOf[OrderInfo])
                })
        orderInfoStream.print(10000)
        //把数据写入到hbase中
        orderInfoStream.foreachRDD(rdd => {
            import org.apache.phoenix.spark._
            rdd.saveToPhoenix(
                "GMALL_ORDER_INFO",
                Seq("ID", "PROVINCE_ID", "CONSIGNEE", "ORDER_COMMENT", "CONSIGNEE_TEL", "ORDER_STATUS", "PAYMENT_WAY", "USER_ID", "IMG_URL", "TOTAL_AMOUNT", "EXPIRE_TIME", "DELIVERY_ADDRESS", "CREATE_TIME", "OPERATE_TIME", "TRACKING_NO", "PARENT_ORDER_ID", "OUT_TRADE_NO", "TRADE_BODY", "CREATE_DATE", "CREATE_HOUR"),
                zkUrl = Option("hadoop102,hadoop103,hadoop104:2181") //Option（null)==None Some(null)
            )
        })
    }
}
