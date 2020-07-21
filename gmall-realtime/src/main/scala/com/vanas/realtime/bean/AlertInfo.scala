package com.vanas.realtime.bean

/**
 * @author Vanas
 * @create 2020-07-18 9:38 上午 
 */

case class AlertInfo(mid: String,
                     uids: java.util.HashSet[String],
                     itemIds: java.util.HashSet[String],
                     events: java.util.List[String],
                     ts: Long)

