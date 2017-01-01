package com.venu.venutheta.Actions;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Madiba on 10/31/2016.
 */

public class ActionLoadMyEvent {

    public List<ParseObject> mdata=null;
    public Boolean error = false;

    public ActionLoadMyEvent(List<ParseObject> mdata, Boolean error) {
        this.mdata = mdata;
        this.error = error;
    }
    public void setError(Boolean error) {
        this.error = error;
    }
}
