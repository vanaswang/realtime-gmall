package com.vanas.realtime.util

import com.vanas.realtime.bean.AlertInfo
import io.searchbox.client.config.HttpClientConfig
import io.searchbox.client.{JestClient, JestClientFactory}
import io.searchbox.core.{Bulk, Index}
import org.apache.spark.rdd.RDD


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


    def main(args: Array[String]): Unit = {


        insertSingle("user", User("ww", 100))

        val it1 = List(("1", User("cc", 1)), ("2", User("dd", 2))).toIterator
        val it2 = List(User("cc", 1), User("dd", 2)).toIterator
        insertBulk("user", it1)

    }

    //批量插入多条
    def insertBulk(index: String, source: Iterator[Object]) = {
        val client: JestClient = factory.getObject
        val bulkbuilder = new Bulk.Builder()
                .defaultIndex(index)
                .defaultType("_doc")


        source.foreach{
            case (id: String, source) =>
                val builder = new Index.Builder(source).id(id)
                bulkbuilder.addAction(builder.build())

            case source =>
                val builder = new Index.Builder(source)
                bulkbuilder.addAction(builder.build())
        }

        client.execute(bulkbuilder.build())
        client.shutdownClient()


    }

    //插入单条数据
    def insertSingle(index: String, source: Object, id: String = null) = {
        val client: JestClient = factory.getObject
        val action: Index = new Index.Builder(source)
                .index(index)
                .`type`("_doc")
                .id(id)
                .build()

        client.execute(action)
        //客户端关闭
        client.shutdownClient()
    }


    implicit class RichES(rdd:RDD[AlertInfo]){
        def saveToES(index: String) ={
                rdd.foreachPartition(it => {
                    //es 每个document都有id，如果id使用分钟数表示：
                    //id重复后面的会覆盖前面的
                    ESUtil.insertBulk(index, it.map(info => (info.mid + ":" + info.ts / 1000 / 60, info)))
                })
        }
    }
}

//case class User(name: String, age: Long)
