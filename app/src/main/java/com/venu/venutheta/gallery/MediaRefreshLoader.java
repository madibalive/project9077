package com.venu.venutheta.gallery;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.venu.venutheta.Actions.ActionMediaCheckIslike;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Madiba on 12/7/2016.
 */

public class MediaRefreshLoader implements Callable<ParseObject> {
    private  String id;
    private  String className;
    public MediaRefreshLoader(String id,String className) {
        this.id=id;
        this.className=className;
    }

    @Override
    public ParseObject call() throws Exception {

        ParseObject obj = ParseObject.createWithoutData(className,id);
        obj.fetch();

        // check if following
        ParseQuery queryR = ParseQuery.getQuery("UserRelations");
        queryR.whereEqualTo("user", ParseUser.getCurrentUser());
        ParseObject relation = queryR.getFirst();
        if (relation !=null){
            ParseRelation<ParseObject> relationLikes = relation.getRelation("likes");
            List<ParseObject> likes= relationLikes.getQuery().find();

            if (likes.contains(obj)){
                EventBus.getDefault().post(new ActionMediaCheckIslike(true));
            }else {
                EventBus.getDefault().post(new ActionMediaCheckIslike(false));
            }
        }

        return obj;
    }
}
