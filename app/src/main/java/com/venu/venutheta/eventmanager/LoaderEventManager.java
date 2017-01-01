package com.venu.venutheta.eventmanager;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Madiba on 12/7/2016.
 */

public class LoaderEventManager {

    public static Observable<List<ParseObject>> loadOnMyEvents(){
        return Observable.create(new Observable.OnSubscribe<List<ParseObject>>() {
            @Override
            public void call(Subscriber<? super List<ParseObject>> subscriber) {

                // Init query
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Peep");
                query = ParseQuery.getQuery("OnTapRequest");
                query.whereEqualTo("from", ParseUser.getCurrentUser());
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

    public static Observable<List<ParseObject>> loadInvites(){
        return Observable.create(new Observable.OnSubscribe<List<ParseObject>>() {
            @Override
            public void call(Subscriber<? super List<ParseObject>> subscriber) {

                // Init query
                ParseQuery<ParseObject> query= ParseQuery.getQuery("EventsVersion3");
                query.whereEqualTo("from", ParseUser.getCurrentUser());
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




}
