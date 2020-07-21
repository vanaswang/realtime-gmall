package com.vanas.realtime.util

import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

/**
 * @author Vanas
 * @create 2020-07-15 11:32 上午 
 */
object RedisUtil {

    //new Jedis("hadoop102",8000)
    val conf = new JedisPoolConfig
    conf.setMaxTotal(100)
    conf.setMaxIdle(30)
    conf.setMinIdle(10)
    conf.setBlockWhenExhausted(true) //阻塞耗尽是否等待
    conf.setMaxWaitMillis(10000)
    conf.setTestOnCreate(true)
    conf.setTestOnBorrow(true) //测试可用性
    conf.setTestOnReturn(true)
    ConfigUtil.getProperty("config.properties", "redis.host")
    ConfigUtil.getProperty("config.properties", "redis.port")
    val pool = new JedisPool(conf, ConfigUtil.getProperty(
        "config.properties", "redis.host"),
        ConfigUtil.getProperty("config.properties",
            "redis.port").toInt)


    def getClient = {

        //pool.getResource
        new Jedis("hadoop102",8000)
    }

    def main(args: Array[String]): Unit = {
        val client: Jedis = getClient
        client.set("k1", "redis")
        client.close() //归还给连接池
        //pool.destroy() 破坏掉连接池
    }
}
