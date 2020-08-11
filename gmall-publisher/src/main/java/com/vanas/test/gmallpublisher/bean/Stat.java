package com.vanas.test.gmallpublisher.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Vanas
 * @create 2020-07-22 11:10 上午
 */
public class Stat {

    private String title;
    private List<Option> options = new ArrayList<>();

    public Stat() {
    }

    public Stat(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void addOption(Option opt, Option... others) {
        options.add(opt);
        Collections.addAll(options, others);
    }
}
