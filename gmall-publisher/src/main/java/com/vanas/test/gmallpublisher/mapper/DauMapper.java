package com.vanas.test.gmallpublisher.mapper;

import java.util.List;
import java.util.Map;

/**
 * @author Vanas
 * @create 2020-07-15 3:16 下午
 */
public interface DauMapper {
    //得到当日总日活
    Long getDau(String date);

    List<Map<String, Object>> getHourDau(String date);
}
