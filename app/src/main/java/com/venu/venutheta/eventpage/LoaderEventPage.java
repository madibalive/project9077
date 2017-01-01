package com.venu.venutheta.eventpage;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.venu.venutheta.Actions.ActionMediaCheckIslike;
import com.venu.venutheta.models.GlobalConstants;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Madiba on 12/16/2016.
 */

public class LoaderEventPage {

    public static Observable<List<ParseObject>> loadGoing(String id, String className){
        return Observable.create(new Observable.OnSubscribe<List<ParseObject>>() {
            @Override
            public void call(Subscriber<? super List<ParseObject>> subscriber) {

                // Init query
                ParseQuery<ParseObject> query = ParseQuery.getQuery(GlobalConstants.CLASS_COMMENT);
                query.whereEqualTo("to", ParseObject.createWithoutData(className,id));
                query.include("from");
                query.orderByAscending("createdAt");
                Timber.d("connecting");
                try {
                    subscriber.onNext(query.find());
                    // save to local store
                    subscriber.onCompleted();


                }catch (Exception e){
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    public static Observable<List<ParseObject>> loadMedia(String id, String className){
        return Observable.create(new Observable.OnSubscribe<List<ParseObject>>() {
            @Override
            public void call(Subscriber<? super List<ParseObject>> subscriber) {
                try {
                    ParseObject obj = ParseObject.createWithoutData(className,id);
                    ParseObject a = new ParseObject("");
                    ParseObject b = new ParseObject("");

                    // Init query
                    ParseQuery<ParseObject> query = ParseQuery.getQuery(GlobalConstants.CLASS_MEDIA);
                    query.whereEqualTo("to", ParseObject.createWithoutData(className,id));
                    query.include("from");
                    query.setLimit(4);
                    query.orderByAscending("createdAt");
                    List<ParseObject>  data = new ArrayList<>();
                    data = query.find();
                    data.add(a);data.add(b);

                    //check status and return fetch event
                    ParseQuery queryR = ParseQuery.getQuery(GlobalConstants.CLASS_USER_RELATION);
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

                    Timber.d("connecting");
                    subscriber.onNext(data);
                    subscriber.onCompleted();

                }catch (Exception e){
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }
}
