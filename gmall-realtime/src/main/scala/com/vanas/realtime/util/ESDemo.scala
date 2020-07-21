package com.vanas.realtime.util

import io.searchbox.client.config.HttpClientConfig
import io.searchbox.client.{JestClient, JestClientFactory}
import io.searchbox.core.Index

/**
 * @author Vanas
 * @create 2020-07-20 1:59 下午 
 */
object ESDemo {

    val esURL = "http://hadoop102:9200"

    def main(args: Array[String]): Unit = {

        //创建es客户端
        val factory = new JestClientFactory
        //配置参数
        val conf = new HttpClientConfig.Builder(esURL)
                .connTimeout(1000 * 60) //连接超时 时间
                .readTimeout(1000 * 10) //读取超时 时间
                .maxTotalConnection(100)
                .multiThreaded(true) //是否允许多线程
                .build()
        factory.setHttpClientConfig(conf)

        val client: JestClient = factory.getObject
        //向es写入数据
//        val source =
//            """
//              |{
//              |  "user":"lisi"
//              |  "age": 20
//              |}
//              |""".stripMargin

        val source =User("zs",30)
        val index: Index = new Index.Builder(source)
                .index("user")
                .`type`("_doc")
                .id("1")
                .build()
        client.execute(index)
        //客户端关闭
        client.shutdownClient()
    }

}

case class User(name: String, age: Long)
