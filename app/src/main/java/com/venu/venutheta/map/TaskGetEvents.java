package com.venu.venutheta.map;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Madiba on 12/10/2016.
 */

public class TaskGetEvents implements Callable<List<ParseObject>> {

    private final LatLng location;

    public TaskGetEvents(LatLng location) {
        this.location = location;
    }


    @Override
    public List<ParseObject> call() throws Exception {
        ParseQuery<ParseObject> tagQuery = ParseQuery.getQuery("EventsVersion3");
        tagQuery.orderByDescending("updateAt");
        tagQuery.setLimit(10);
        tagQuery.whereNear("cordinate",new ParseGeoPoint(location.latitude,location.longitude));
        return tagQuery.find();

    }
}
