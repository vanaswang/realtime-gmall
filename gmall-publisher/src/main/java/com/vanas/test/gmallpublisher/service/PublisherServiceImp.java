package com.vanas.test.gmallpublisher.service;

import com.vanas.test.gmallpublisher.mapper.DauMapper;
import com.vanas.test.gmallpublisher.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vanas
 * @create 2020-07-15 3:31 下午
 */
@Service
public class PublisherServiceImp implements PublisherService {

    @Autowired
    DauMapper dau;

    @Override
    public Long getDau(String date) {
        return dau.getDau(date);
    }

    /*
       数据层
       List(Map("loghour":"10",count:100),Map,....)
        List<Map<String, Object>> getHourDau(String date);
        Map("10"->100 ,"11"->200,"12"->100)
    */
    @Override
    public Map<String, Long> getHourDau(String date) {
        List<Map<String, Object>> hourDau = dau.getHourDau(date);
        Map<String, Long> result = new HashMap<>();
        for (Map<String, Object> map : hourDau) {
            String key = map.get("LOGHOUR").toString();
            Long value = (Long) map.get("COUNT");
            result.put(key, value);
        }
        return result;
    }

    @Autowired
    OrderMapper order;

    @Override
    public Double getTotalAmount(String date) {
        Double result = order.getTotalAmount(date);
        return result == null ? 0 : result;
    }

    @Override
    public Map<String, Double> getHourAmount(String date) {
        List<Map<String, Object>> hourAmountList = order.getHourAmount(date);
        HashMap<String, Double> resultMap = new HashMap<>();
        for (Map<String, Object> map : hourAmountList) {
            String key = (String) map.get("CREATE_HOUR");
//            Double value = ((BigDecimal) map.get("SUM")).doubleValue();
            Double value = ((BigDecimal) map.get("SUM")).doubleValue();
            resultMap.put(key,10.00);
        }
        return resultMap ;
    }
}
