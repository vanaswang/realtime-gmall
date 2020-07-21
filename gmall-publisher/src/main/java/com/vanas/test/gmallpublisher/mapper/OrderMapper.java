package com.vanas.test.gmallpublisher.mapper;

import java.util.List;
import java.util.Map;

/**
 * @author Vanas
 * @create 2020-07-17 3:00 下午
 */
public interface OrderMapper {

    //当日销售总额
    Double getTotalAmount(String date);

    List<Map<String,Object>> getHourAmount(String date);
}
