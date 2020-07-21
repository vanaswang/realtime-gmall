//package com.vanas.utils;
//
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.otter.canal.protocol.CanalEntry;
//import com.vanas.common.Constant;
//
//import java.util.List;
//
///**
// * @author Vanas
// * @create 2020-07-16 10:01 下午
// */
//public class CanalHandler {
//    CanalEntry.EventType eventType;
//
//    String tableName;
//
//    List<CanalEntry.RowData> rowDataList;
//
//    public CanalHandler(CanalEntry.EventType eventType, String tableName, List<CanalEntry.RowData> rowDataList) {
//        this.eventType = eventType;
//        this.tableName = tableName;
//        this.rowDataList = rowDataList;
//    }
//
//    public void handle() {
//        //下单操作
//        if ("order_info".equals(tableName) && CanalEntry.EventType.INSERT == eventType) {
//            rowDateList2Kafka(Constant.KAFKA_TOPIC_ORDER);
//        } else if ("user_info".equals(tableName) && (CanalEntry.EventType.INSERT == eventType || CanalEntry.EventType.UPDATE == eventType)) {
//            rowDateList2Kafka(Constant.KAFKA_TOPIC_USER);
//        }
//
//    }
//
//    private void rowDateList2Kafka(String kafkaTopic) {
//        for (CanalEntry.RowData rowData : rowDataList) {
//            List<CanalEntry.Column> columnsList = rowData.getAfterColumnsList();
//            JSONObject jsonObject = new JSONObject();
//            for (CanalEntry.Column column : columnsList) {
//
//                System.out.println(column.getName() + "::::" + column.getValue());
//                jsonObject.put(column.getName(), column.getValue());
//            }
//
//            MyKafkaSender.send(kafkaTopic, jsonObject.toJSONString());
//        }
//
//    }
//
//}
