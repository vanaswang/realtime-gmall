package com.vanas.test.gmalllogger.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vanas.common.Constant;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author Vanas
 * @create 2020-07-13 7:37 下午
 */
//@RestController=@Controller+@ResponseBody
@RestController
@Slf4j
public class LoggerController {
    //http://localhost:8080/log?log=abc
    // @GetMapping("/log")
    //public String doLog(@RequestParam("log") String a) {
    // public String doLog(String log, String aaa) { //变量名和key一致注解也可以省
    @PostMapping("/log")
    public String doLog(String log) { //变量名和key一致注解也可以省
        //System.out.println(log);
        //System.out.println(aaa);


        //1。给日志添加一个时间戳
        log = addTS(log);
        //System.out.println(log);
        //2。数据落盘（为离线数据做准备）
        saveToDisk(log);

        //3。把数据写入到kafka，需要写入到topic
        sendToKafka(log);

        return "ok";
    }

    /**
     * 把日志发送到kafka
     * 不同的日志写入到不同的topic
     *
     * @param log
     */
    @Autowired
    KafkaTemplate kafka;

    private void sendToKafka(String log) {
        if (log.contains("\"startup\"")) {
            kafka.send(Constant.START_TOPIC, log);
        } else {
            kafka.send(Constant.EVENT_TOPIC, log);
        }
    }

    /**
     * 把日志写入到磁盘
     *
     * @param log
     */
    Logger logger = LoggerFactory.getLogger(LoggerController.class);

    private void saveToDisk(String log) {
        logger.info(log);
    }

    /**
     * 给日志添加时间戳
     *
     * @param log
     * @return
     */
    private String addTS(String log) {
        JSONObject obj = JSON.parseObject(log);
        obj.put("ts", System.currentTimeMillis());
        return JSON.toJSONString(obj);
    }
}

