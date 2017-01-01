package com.venu.venutheta.models;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Madiba on 10/9/2016.
 */

public class TrendingModel {

    public static final int LIVE_NOW = 3;
    public static final int GOSSIP = 2;
    public static final int EVENTS = 4;
    public static final int TRENDING = 1;

    private String mtitle;
    private int Type;
    private List<ParseObject> data = new ArrayList<>();

    public TrendingModel(String mtitle, int type) {
        this.mtitle = mtitle;
        Type = type;
    }

    public String getMtitle() {
        return mtitle;
    }

    public void setMtitle(String mtitle) {
        this.mtitle = mtitle;
    }


    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public List<ParseObject> getdata() {
        return data;
    }

    public void setdata(List<ParseObject> data) {
        this.data = data;
    }


}
