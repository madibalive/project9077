package com.venu.venutheta.Actions;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Madiba on 10/31/2016.
 */

public class ActionLoadMedia {

    public List<ParseObject> mdata=null;
    public Boolean error = false;
    public int limit  = 0;
    public int skip    =0;

    public ActionLoadMedia(List<ParseObject> mdata, Boolean error, int limit, int skip) {
        this.mdata = mdata;
        this.error = error;
        this.limit = limit;
        this.skip = skip;
    }
}
