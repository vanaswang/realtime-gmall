package com.vanas.realtime.util

import java.io.InputStream
import java.util.Properties

/**
 * @author Vanas
 * @create 2020-07-15 11:07 上午 
 */
object ConfigUtil {


    def getProperty(fileName: String, name: String) = {
        val is: InputStream = ConfigUtil.getClass.getClassLoader.getResourceAsStream(fileName)
        val ps = new Properties()
        ps.load(is)
        ps.getProperty(name)
    }


    def main(args: Array[String]): Unit = {
        println(getProperty("config.properties", "kafka.servers"))
    }

}
