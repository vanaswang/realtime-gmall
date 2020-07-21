package com.vanas.canal

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

/**
 * @author Vanas
 * @create 2020-07-17 2:03 下午 
 */
object MykafkaUtil {


    val pops = new Properties()

    pops.setProperty("bootstrap.servers", "hadoop102:9092,hadoop103:9092,hadoop104:9092")
    // key序列化
    pops.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    // value序列化
    pops.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](pops)

    def sendToKafka(topic: String, content: String) = {
        producer.send(new ProducerRecord[String, String](topic, content))
    }

}
