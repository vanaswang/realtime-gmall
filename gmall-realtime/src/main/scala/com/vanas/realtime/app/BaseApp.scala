package com.vanas.realtime.app

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * @author Vanas
 * @create 2020-07-17 2:16 下午 
 */
trait BaseApp {
    def main(args: Array[String]): Unit = {
        //1.创建一个streamingContext
        val conf = new SparkConf().setMaster("local[2]").setAppName("DauApp")
        val ssc = new StreamingContext(conf, Seconds(3))

        run(ssc)
        //6.开启流
        ssc.start()
        //7.阻止main退出
        ssc.awaitTermination()
    }


    def run(ssc: StreamingContext): Unit

}
