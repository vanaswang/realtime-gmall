package com.vanas.test.gmallpublisher.service;
import java.util.Map;

/**
 * @author Vanas
 * @create 2020-07-15 3:30 下午
 */
public interface PublisherService {
    //获取总的日活
    Long getDau(String date);

    Map<String, Long> getHourDau(String date);

    //获取指定日期的销售总额
    Double getTotalAmount(String date);

    //获取小时的销售额
    Map<String, Double> getHourAmount(String date);

}
