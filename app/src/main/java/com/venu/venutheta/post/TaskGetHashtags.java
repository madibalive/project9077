package com.venu.venutheta.post;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Madiba on 12/10/2016.
 */

public class TaskGetHashtags implements Callable<List<ParseObject>> {

    private final String term;

    public TaskGetHashtags(String term) {
        this.term = term;
    }


    @Override
    public List<ParseObject> call() throws Exception {
        ParseQuery<ParseObject> tagQuery = ParseQuery.getQuery("EventsVersion3");
        tagQuery.orderByDescending("updateAt");
        tagQuery.setLimit(8);
        tagQuery.whereStartsWith("tag",term);
        return tagQuery.find();

    }
}
