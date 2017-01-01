package com.venu.venutheta.profile;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Madiba on 12/7/2016.
 */

public class ProfileLoader {


    public static Observable<List<ParseObject>> loadGallery(String id, String className, int skip, Date date){
        return Observable.create(new Observable.OnSubscribe<List<ParseObject>>() {
            @Override
            public void call(Subscriber<? super List<ParseObject>> subscriber) {

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Activities");
                query.whereEqualTo("to", ParseUser.createWithoutData(ParseUser.class,id));
                query.orderByAscending("Created");
                query.setLimit(30);
                query.whereLessThan("createdAt",date);
                query.setSkip(skip);

                Timber.d("connecting");
                try {
                    subscriber.onNext(query.find());
                    // save to local store
                    subscriber.onCompleted();

                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    public static Observable<List<ParseObject>> loadEvent(String id, String className, int skip, Date date){
        return Observable.create(new Observable.OnSubscribe<List<ParseObject>>() {
            @Override
            public void call(Subscriber<? super List<ParseObject>> subscriber) {

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Activities");
                query.whereEqualTo("to", ParseUser.createWithoutData(ParseUser.class,id));
                query.orderByAscending("Created");
                query.setLimit(30);
                query.whereLessThan("createdAt",date);
                query.setSkip(skip);

                Timber.d("connecting");
                try {
                    subscriber.onNext(query.find());
                    // save to local store
                    subscriber.onCompleted();


                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    public static Observable<List<ParseObject>> follow(String id){
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

}
