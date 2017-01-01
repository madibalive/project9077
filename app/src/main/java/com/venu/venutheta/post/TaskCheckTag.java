package com.venu.venutheta.post;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.concurrent.Callable;

/**
 * Created by Madiba on 12/10/2016.
 */

public class TaskCheckTag implements Callable<Boolean>{

    private final String term;

    TaskCheckTag(String term) {
        this.term = term;
    }


    @Override
    public Boolean call() throws Exception {
        ParseQuery<ParseObject> hashtagQuery = ParseQuery.getQuery("EventsTest");
        hashtagQuery.whereStartsWith("tag",term);
        ParseObject data = hashtagQuery.getFirst();

        if (data !=null){
            return true;
        }else
            return false;
    }
}
