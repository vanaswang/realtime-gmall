package com.vanas.client;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.InvalidProtocolBufferException;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author Vanas
 * @create 2020-07-16 9:07 下午
 */
public class CanalClient {
    public static void main(String[] args) {
        //获取Canal连接
        CanalConnector canalConnector = CanalConnectors.newSingleConnector(new InetSocketAddress("hadoop102", 11111), "example", "", "");

        //抓取数据
        while (true) {
            //连接canal
            canalConnector.connect();
            //指定订阅的数据库(gmall)
            canalConnector.subscribe("gamll_realtime.*");
            //执行抓取数据操作
            Message message = canalConnector.get(1024);
            //判断当前抓取的数据是否为空，如果为空，则休息一会
            if (message.getEntries().size() == 0) {
                System.out.println("没有数据，休息一会！");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                //解析message
                for (CanalEntry.Entry entry : message.getEntries()) {
                    //对当前entry类型做判断，只需要数据变化的内容,事务的开启与关闭以及心跳信息则过滤掉
                    if (CanalEntry.EntryType.ROWDATA.equals(entry.getEntryType())) {
                        //获取当前数据的表名
                        String tableName = entry.getHeader().getTableName();
                        try {
                            //将stroeValue反序列化
                            CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                            //取出行集以及事件类型
                            List<CanalEntry.RowData> rowDatasList = rowChange.getRowDatasList();
                            CanalEntry.EventType eventType = rowChange.getEventType();
                            //处理数据
                            handler(tableName, rowDatasList, eventType);
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
    }

    //处理监控的Mysql数据，解析并发送至Kafka
    private static void handler(String tableName, List<CanalEntry.RowData> rowDatasList, CanalEntry.EventType eventType) {
        //判断是否为订单表
        if (("order_info".equals(tableName))) {
            //取下单数据，即只要新增的数据
            if (CanalEntry.EventType.INSERT.equals(eventType)) {
                //遍历rowDataList
                for (CanalEntry.RowData rowData : rowDatasList) {
                    //创建JSON对象用于存放多个列的数据
                    JSONObject jsonObject = new JSONObject();
                    //遍历修改之后的数据列
                    for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
                        jsonObject.put(column.getName(), column.getValue());
                    }
                    //打印当前行的数据,写入Kafka
                    System.out.println(jsonObject.toString());
                }
            }
        }
    }
}
