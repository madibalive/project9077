package com.venu.venutheta.mainfragment;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.venu.venutheta.models.GlobalConstants;
import com.venu.venutheta.models.TrendingModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Madiba on 12/8/2016.
 */

public class LoaderMainFragment {

    public static Observable<List<ParseObject>> LoadChallange(Boolean global){

        return Observable.create(new Observable.OnSubscribe<List<ParseObject>>() {
            @Override
            public void call(Subscriber<? super List<ParseObject>> subscriber) {
                Timber.e("loadin channel challange ");

                List<ParseObject> mDatas = new ArrayList<>();
                ParseObject me;

                ParseQuery<ParseObject> followersQuery = ParseQuery.getQuery("FollowVersion3");
                followersQuery.whereEqualTo("to", ParseUser.getCurrentUser());

                ParseQuery<ParseObject> challangeQueryMe = ParseQuery.getQuery("Peep");
                challangeQueryMe.whereStartsWith(GlobalConstants.STRING_HASHTAG, "venuchallange");
                challangeQueryMe.whereEqualTo("from", ParseUser.getCurrentUser());
                challangeQueryMe.include(GlobalConstants.OBJ_FROM);

                ParseQuery<ParseObject> query;

                if (global) {

                    query = ParseQuery.getQuery("Peep");
                    query.whereStartsWith(GlobalConstants.STRING_HASHTAG, "venuchallange");
                    query.setLimit(20);
                    query.include(GlobalConstants.OBJ_FROM);
                    query.orderByAscending("createdAt");

                } else {

                    query = ParseQuery.getQuery("Gossip");
                    query.whereMatchesKeyInQuery(GlobalConstants.OBJ_FROM, GlobalConstants.OBJ_FROM, followersQuery);
                    query.whereStartsWith(GlobalConstants.STRING_HASHTAG, "venuchallange");
                    query.setLimit(20);
                    query.include(GlobalConstants.OBJ_FROM);
                    query.orderByAscending("createdAt");

                }

                try {
                    me = challangeQueryMe.getFirst();
                    mDatas = query.find();

                    if (me != null) {
                        if (mDatas.contains(me)) {
                            subscriber.onNext(mDatas);
                        } else {
                            mDatas.add(me);
                            subscriber.onNext(mDatas);
                        }
                    } else {
                        subscriber.onNext(mDatas);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                    subscriber.onError(e);

                } finally {
                    subscriber.onCompleted();

                }
            }
        }).subscribeOn(Schedulers.io());
    }

    public static Observable<List<ParseObject>> LoadChannel(){

        return Observable.create(new Observable.OnSubscribe<List<ParseObject>>() {
            @Override
            public void call(Subscriber<? super List<ParseObject>> subscriber) {

                Timber.e("started channel ");

                ParseQuery<ParseObject> channelQuery = ParseQuery.getQuery("Peep");
                channelQuery.setLimit(4);
                channelQuery.include("image");
                channelQuery.whereExists("image");
                channelQuery.orderByAscending("pos");
                try {
                    List<ParseObject> mdata = channelQuery.find();

                    subscriber.onNext(mdata);

                } catch (ParseException e) {
                    e.printStackTrace();
                    subscriber.onError(e);

                } finally {
                    subscriber.onCompleted();

                }
            }
        }).subscribeOn(Schedulers.io());

    }

    public static Observable<List<TrendingModel>> loadrend(){

        return Observable.create(new Observable.OnSubscribe<List<TrendingModel>>() {
            @Override
            public void call(Subscriber<? super List<TrendingModel>> subscriber) {
                List<TrendingModel> mdata = new ArrayList<>();
                Timber.e("started trend ");
                Calendar cal = new GregorianCalendar();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                Date today = cal.getTime();

                cal.add(Calendar.DAY_OF_MONTH, 1);

                Date tomorrow = cal.getTime();

                ParseQuery<ParseObject> queryEvents= ParseQuery.getQuery("Events");
                queryEvents.orderByAscending("createdAt");
                queryEvents.setLimit(10);
                queryEvents.whereGreaterThanOrEqualTo("createdAt", today);
                queryEvents.whereLessThan("createdAt", tomorrow);

                ParseQuery<ParseObject> queryGossip= ParseQuery.getQuery("Gossip");
                queryGossip.orderByAscending("createdAt");
                queryGossip.orderByDescending("reaction");
                queryGossip.whereLessThanOrEqualTo("expiry",today);
                queryGossip.setLimit(6);

                ParseQuery<ParseObject> queryTopEvents= ParseQuery.getQuery("Events");
                queryTopEvents.orderByDescending("venuBonus");
                queryTopEvents.setLimit(10);

                try {

                    TrendingModel livenow = new TrendingModel("Live Now",TrendingModel.LIVE_NOW);
                    livenow.setdata(queryEvents.find());

                    TrendingModel mGossip = new TrendingModel("Trending Gossip",TrendingModel.GOSSIP);
                    mGossip.setdata(queryGossip.find());

                    TrendingModel event = new TrendingModel("Top Event",TrendingModel.EVENTS);
                    event.setdata(queryTopEvents.find());

                    mdata.add(livenow);
                    mdata.add(mGossip);
                    mdata.add(event);

                    subscriber.onNext(mdata);
                    subscriber.onCompleted();

                }catch (Exception e){
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

}
