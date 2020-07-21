package com.vanas.realtime.util

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

/**
 * @author Vanas
 * @create 2020-07-14 4:39 下午 
 */
object MyKafkaUtil {


    val kafkaParams = Map[String, Object](
        "bootstrap.servers" -> ConfigUtil.getProperty("config.properties", "kafka.servers"),
        "key.deserializer" -> classOf[StringDeserializer],
        "value.deserializer" -> classOf[StringDeserializer],
        "group.id" -> ConfigUtil.getProperty("config.properties", "group.id"),
        "auto.offset.reset" -> "latest",
        "enable.auto.commit" -> (true: java.lang.Boolean)
    )

    def getKafkaStream(ssc: StreamingContext, topic: String) = {
        KafkaUtils.createDirectStream[String, String](
            ssc,
            PreferConsistent, //标配
            Subscribe[String, String](Set(topic), kafkaParams)
        ).map(_.value())

    }

}
