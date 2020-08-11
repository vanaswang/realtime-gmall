package com.vanas.test.gmallpublisher.service;

        import java.io.IOException;
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


    //从ES读数据，返回需要的数据返回Controller
    /*
        "total":200,
        "agg":Map("M"->100,"F"->100),
        "detail":List(Map(一行记录),Map(...))
     */
    //http://localhost:8070/sale_detail?date=2019-05-20&&startpage=1&&size=5&&keyword=手机小米
    Map<String, Object> getSaleDetailAndAgg(String date,
                                            String keyword,
                                            int startPage,
                                            int sizePerPage,
                                            String aggField,
                                            int aggCount) throws IOException;

}
