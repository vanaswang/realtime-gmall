package com.vanas.test.gmallpublisher.service;

import com.vanas.test.gmallpublisher.Util.ESUtil;
import com.vanas.test.gmallpublisher.mapper.DauMapper;
import com.vanas.test.gmallpublisher.mapper.OrderMapper;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
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
            resultMap.put(key, 10.00);
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getSaleDetailAndAgg(String date,
                                                   String keyword,
                                                   int startPage,
                                                   int sizePerPage,
                                                   String aggField,
                                                   int aggCount) throws IOException {
        String dsl = ESUtil.getDSL(date, keyword, aggField, aggCount, startPage, sizePerPage);
        // 先获取es客户端
        JestClient client = ESUtil.getESClient();
        // 创建查询对象
        Search search = new Search.Builder(dsl)
                .addIndex("gmall_sale_detail")
                .addType("_doc")
                .build();
        // 执行查询
        SearchResult searchResult = client.execute(search);

        // 解析查询结果
        Map<String, Object> result = new HashMap<>();
        // 1. 获取查询的总数
        Long total = searchResult.getTotal();
        result.put("total", total);
        // 2. 获取详情数据
        ArrayList<Map> detail = new ArrayList<>();
        List<SearchResult.Hit<HashMap, Void>> hits = searchResult.getHits(HashMap.class);
        for (SearchResult.Hit<HashMap, Void> hit : hits) {
            HashMap source = hit.source;
            detail.add(source);
        }
        result.put("detail", detail);
        // 3. 获取就会结果
        HashMap<String, Long> agg = new HashMap<>();
        List<TermsAggregation.Entry> buckets = searchResult
                .getAggregations()
                .getTermsAggregation("group_by_" + aggField)
                .getBuckets();
        for (TermsAggregation.Entry bucket : buckets) {
            String key = bucket.getKey();
            Long value = bucket.getCount();
            agg.put(key, value);
        }
        result.put("agg", agg);
        // 把需要数据封装到Map中返回
        return result;
    }

}
