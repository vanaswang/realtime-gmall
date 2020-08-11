package com.vanas.realtime.util

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author Vanas
 * @create 2020-07-24 8:48 上午 
 */
object MapJoin {
    def main(args: Array[String]): Unit = {

        val conf: SparkConf = new SparkConf().setAppName("mapjoin").setMaster("local[2]")
        val sc = new SparkContext(conf)
        val rdd1: RDD[(String, Int)] = sc.parallelize(List(("hello", 1), ("hello", 1), ("world", 1), ("vava", 1)))
        val rdd2: RDD[(String, Int)] = sc.parallelize(List(("hello", 2), ("hello", 1), ("world", 2), ("vava", 1), ("abc", 2)))

        //val result: RDD[(String, (Int, Int))] = rdd1.join(rdd2)


        //mapjoin:其中小的rdd的数据拉到驱动，把结果广播出去
        val bd = sc.broadcast(rdd2.collect())
        //对比较大的rdd做map

        //右连接
        val result: RDD[(String, (Option[Int], Int))] = rdd1.flatMap {
            case (k, v1) =>
                val arr2: Array[(String, Int)] = bd.value

                arr2.filter(_._1 == k).map {
                    case (k, v2) => {
                        (k, (Option(v1), v2))
                    }
                } ++
                        arr2.filter(_._1 != k).map {
                            case (k2, v2) => {
                                (k2, (None, v2))
                            }
                        }
        }

        //leftjoin
//        val result = rdd1.flatMap {
//            case (k, v1) =>
//                val arr2: Array[(String, Int)] = bd.value
//                if (!arr2.map(_._1).contains(k)) {
//                    Array((k, (v1, None)))
//                } else {
//                    arr2.filter(_._1 == k).map {
//                        case (k, v2) => {
//                            (k, (v1, Option(v2)))
//                        }
//                    }
//                }
//        }


        //        val result = rdd1.flatMap {
        //            case (k, v1) =>
        //                val arr2: Array[(String, Int)] = bd.value
        //                arr2.filter(_._1 == k).map {
        //                    case (k, v2) => {
        //                        (k, (v1, v2))
        //                    }
        //                }
        //        }
        result.collect().foreach(println)
        sc.stop()
        /*
        (hello,(1,2))
        (hello,(1,1))
        (hello,(1,2))
        (hello,(1,1))
        (world,(1,2))
        (vava,(1,1))
         */
    }

}
