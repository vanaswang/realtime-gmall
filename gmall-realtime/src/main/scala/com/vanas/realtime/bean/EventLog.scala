package com.vanas.realtime.bean

import java.text.SimpleDateFormat
import java.util.Date

/**
 * @author Vanas
 * @create 2020-07-18 8:31 上午 
 */
case class EventLog(mid: String,
                    uid: String,
                    appId: String,
                    area: String,
                    os: String,
                    logType: String,
                    eventId: String,
                    pageId: String,
                    nextPageId: String,
                    itemId: String,
                    ts: Long,
                    var logDate: String,
                    var logHour: String){
    private val date = new Date(ts)
    logDate = new SimpleDateFormat("yyyy-MM-dd").format(date)
    logHour = new SimpleDateFormat("HH").format(date)

}

