package com.vanas.test.gmallpublisher.Util

import io.searchbox.client.JestClientFactory
import io.searchbox.client.config.HttpClientConfig


/**
 * @author Vanas
 * @create 2020-07-20 1:59 下午 
 */
object ESUtil {

    val esURL = "http://hadoop102:9200"
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


    def  getESClient= factory.getObject

    def getDSL(date:String,keyword:String,aggField:String,aggSize:Int,startPage:Int,sizePerPage:Int)={

        s"""
           |{
           |  "query": {
           |    "bool": {
           |      "filter": {
           |        "term": {
           |          "dt": "${date}"
           |        }
           |      },
           |      "must": [
           |        {"match": {
           |          "sku_name": "${keyword}"
           |        }}
           |      ]
           |    }
           |  },
           |  "aggs": {
           |    "group_by_${aggField}": {
           |      "terms": {
           |        "field": "${aggField}",
           |        "size": ${aggSize}
           |      }
           |    }
           |  },
           |  "from": ${(startPage - 1) * sizePerPage} ,
           |  "size": ${sizePerPage}
           |}
           |""".stripMargin

    }
}

//case class User(name: String, age: Long)
