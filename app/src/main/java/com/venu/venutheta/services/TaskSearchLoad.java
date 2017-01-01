package com.venu.venutheta.services;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.venu.venutheta.models.SearchModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Madiba on 12/7/2016.
 */

public class TaskSearchLoad implements Callable<List<SearchModel>> {
    private  String searchTerm;
    public TaskSearchLoad(String searchTerm) {
        this.searchTerm=searchTerm;
    }

    @Override
    public List<SearchModel> call() throws Exception {
        List<SearchModel> mSearchDatas = new ArrayList<>();

        ParseQuery<ParseUser> userQuery= ParseUser.getQuery();
        userQuery.whereStartsWith("username", searchTerm);

        ParseQuery<ParseObject> GossipQuery= ParseQuery.getQuery("EventsTest");
        GossipQuery.whereStartsWith("title", searchTerm);
        GossipQuery.orderByAscending("shares");
        GossipQuery.addAscendingOrder("likes");

        ParseQuery<ParseObject> eventQuery= ParseQuery.getQuery(GlobalConstants.CLASS_EVENT);
        eventQuery.whereStartsWith("title", searchTerm);
        eventQuery.orderByAscending("shares");
        eventQuery.addAscendingOrder("likes");


        SearchModel gossips = new SearchModel("Gossip", SearchModel.GOSSIP);
        gossips.setmData(GossipQuery.find());

        SearchModel peoples = new SearchModel("People", SearchModel.PEOPLE);
        peoples.setUsers(userQuery.find());

        SearchModel events = new SearchModel("Event", SearchModel.EVENT);
        events.setmData(eventQuery.find());

        mSearchDatas.add(gossips);
        mSearchDatas.add(peoples);
        mSearchDatas.add(events);

        return mSearchDatas;
    }
}
