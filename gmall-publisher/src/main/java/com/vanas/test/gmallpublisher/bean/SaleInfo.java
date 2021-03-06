package com.vanas.test.gmallpublisher.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Vanas
 * @create 2020-07-22 11:16 上午
 */
public class SaleInfo {
    private long total;
    private List<Stat> stats = new ArrayList<>();
    private List<Map> detail;

    public SaleInfo() {
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<Stat> getStats() {
        return stats;
    }

    public List<Map> getDetail() {
        return detail;
    }

    public void setDetail(List<Map> detail) {
        this.detail = detail;
    }

    public void addStat(Stat stat, Stat... other) {
        stats.add(stat);
        Collections.addAll(stats, other);
    }
}
