package com.venu.venutheta.mainfragment;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.venu.venutheta.models.GlobalConstants;
import com.venu.venutheta.models.ModelFeedItem;
import com.venu.venutheta.utils.TimeUitls;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Madiba on 12/7/2016.
 */

public class LoaderFeed {


    public static Observable<List<ModelFeedItem>> loadLocal(){
        return Observable.create(new Observable.OnSubscribe<List<ModelFeedItem>>() {
            @Override
            public void call(Subscriber<? super List<ModelFeedItem>> subscriber) {

                Timber.e("feed");

                List<ParseObject> data = new ArrayList<>();
                List<ModelFeedItem> returnData = new ArrayList<>();

                //INITIAL VARIABLES AND BUCKETS
                List<ParseObject> RxLove = new ArrayList<ParseObject>();
                List<ParseObject> RxStars = new ArrayList<ParseObject>();
                List<ParseObject> RxThumbs = new ArrayList<ParseObject>();

                //init followers query
                ParseQuery<ParseObject> followersQuery = ParseQuery.getQuery("FollowVersion3");
                followersQuery.whereEqualTo("from", ParseUser.getCurrentUser());

                //non private query
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Feed");
                query.whereMatchesKeyInQuery(GlobalConstants.OBJ_FROM, "to", followersQuery);
                query.whereEqualTo("isPrivate", false);

                // user own query
                ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("Feed");
                userQuery.whereEqualTo(GlobalConstants.OBJ_FROM, ParseUser.getCurrentUser());

                //private query
                ParseQuery<ParseObject> privateQuery = ParseQuery.getQuery("Feed");
                privateQuery.whereEqualTo("isPrivate", true);
                privateQuery.whereEqualTo("PrivateList", ParseUser.getCurrentUser().getObjectId());

                // generate final query
                List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
                queries.add(query);
                queries.add(userQuery);
                queries.add(privateQuery);

                // extra condition on mainquery
                ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
                mainQuery.orderByDescending("createdAt");
                mainQuery.whereLessThan("createdAt", TimeUitls.getCurrentDate());
                mainQuery.whereLessThanOrEqualTo("expiryDate", TimeUitls.getCurrentDate());
                mainQuery.include("from");
                mainQuery.include("object");
                mainQuery.whereExists("from");

                // relations queyr
                ParseQuery queryR = ParseQuery.getQuery("UserRelations");
                queryR.whereEqualTo("user", ParseUser.getCurrentUser());

                try {

                    ParseObject relation = queryR.getFirst();

                    if (relation != null) {
                        ParseRelation<ParseObject> relationLikes = relation.getRelation("likes");
                        RxLove = relationLikes.getQuery().find();
                        ParseRelation<ParseObject> relationThumbs = relation.getRelation("thumbsUps");
                        RxThumbs = relationThumbs.getQuery().find();
                        ParseRelation<ParseObject> Relationfav = relation.getRelation("favorites");
                        RxStars = Relationfav.getQuery().find();
                    }

                    data = mainQuery.find();

                    for (ParseObject m : data
                            ) {
                        Boolean isShared = false;
                        ModelFeedItem item = new ModelFeedItem();

                        ParseObject dataItem = m.getParseObject("object");
                        // set name,avatar,date
                        item.setName(m.getParseUser("from").getUsername());
                        item.setAvatar(m.getParseUser("from").getParseFile("avatar").getUrl());
                        item.setDate(dataItem.getDate("createdAt"));
                        item.setDateToString(TimeUitls.getRelativeTime(item.getDate()));


                        // set if shared
                        if (m.getInt("type") == 3) {
                            dataItem = dataItem.getParseObject("object"); // override object here
                            item.setShared(true);
                        } else {
                            item.setShared(false);
                        }

                        // set gossip details
                        if (dataItem.getClassName().equals("Gossip")) {

                            item.setGpTitle(dataItem.getString("title"));
                            item.setReactions(dataItem.getInt("reactions"));
                            item.setGpChatId(dataItem.getInt("chatId"));
                            item.setGpElapseTime(dataItem.getDate("expiryDate"));
                            item.setGpElapseTimeToString(TimeUitls.elapseTime(dataItem.getDate("expiryDate")));
                            if (RxThumbs.contains(dataItem)) {
                                item.setGpIsThumbsUp(true);
                            } else {
                                item.setGpIsThumbsUp(true);
                            }
                            item.setType(ModelFeedItem.TYPP_GOSSIP);


                        } else {

                            item.setHashtag(dataItem.getString("hashtag"));
                            item.setComment(dataItem.getInt("comments"));
                            item.setReactions(dataItem.getInt("reactions"));
                            item.setImage(dataItem.getParseFile("image").getUrl());

                            if (dataItem.getClassName().equals("Events")) {
                                item.setEvTitle(dataItem.getString("title"));
                                item.setEvLocation(dataItem.getString("address"));
                                item.setStatus(TimeUitls.getLiveBadgeText(dataItem.getDate("date")));
                                item.setEvTime(dataItem.getDate("time"));
                                item.setEvTimeToString(TimeUitls.Format12_34(dataItem.getDate("time")));
                                item.setEvDate(dataItem.getDate("date"));
                                item.setEvDateToString(TimeUitls.Format12Dec(dataItem.getDate("date")));
                                item.setEvLocation(dataItem.getString("location"));
                                item.setType(ModelFeedItem.TYPP_EVENT);
                                if (RxStars.contains(dataItem)) {
                                    item.setEvIsInterest(true);
                                } else {
                                    item.setEvIsInterest(true);
                                }

                                // set price

                            } else if (dataItem.getClassName().equals("media")) {
                                if (RxLove.contains(dataItem)) {
                                    item.setPpIsLike(true);
                                } else {
                                    item.setPpIsLike(true);
                                }

                                if (dataItem.getInt("type") == 1) {
                                    item.setPpVideo(1);
                                    item.setUrl(dataItem.getString("url"));
                                    item.setType(ModelFeedItem.TYPP_MEDIA_VIDEO);

                                } else {
                                    item.setPpVideo(0);
                                    item.setType(ModelFeedItem.TYPP_MEDIA_IMAGE);

                                }
                            }
                        }

                        returnData.add(item);
                    }

                    subscriber.onNext(returnData);
                } catch (ParseException e) {
                    e.printStackTrace();

                    subscriber.onError(e);
                } finally {
                    subscriber.onCompleted();
                }
            }

        }).subscribeOn(Schedulers.io());

    }


    public static Observable<List<ModelFeedItem>> load(int skip, Date date){
        return Observable.create(new Observable.OnSubscribe<List<ModelFeedItem>>() {
            @Override
            public void call(Subscriber<? super List<ModelFeedItem>> subscriber) {

                Timber.e("feed");
                List<ParseObject> data = new ArrayList<>();
                List<ModelFeedItem> returnData = new ArrayList<>();

                //INITIAL VARIABLES AND BUCKETS
                List<ParseObject> RxLove = new ArrayList<ParseObject>();
                List<ParseObject> RxStars = new ArrayList<ParseObject>();
                List<ParseObject> RxThumbs = new ArrayList<ParseObject>();

                //init followers query
                ParseQuery<ParseObject> followersQuery = ParseQuery.getQuery("FollowVersion3");
                followersQuery.whereEqualTo("from", ParseUser.getCurrentUser());

                //non private query
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Feed");
                query.whereMatchesKeyInQuery(GlobalConstants.OBJ_FROM, "to", followersQuery);
                query.whereEqualTo("isPrivate", false);

                // user own query
                ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("Feed");
                userQuery.whereEqualTo(GlobalConstants.OBJ_FROM, ParseUser.getCurrentUser());

                //private query
                ParseQuery<ParseObject> privateQuery = ParseQuery.getQuery("Feed");
                privateQuery.whereEqualTo("isPrivate", true);
                privateQuery.whereEqualTo("PrivateList", ParseUser.getCurrentUser().getObjectId());

                // generate final query
                List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
                queries.add(query);
                queries.add(userQuery);
                queries.add(privateQuery);

                // extra condition on mainquery
                ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
                mainQuery.orderByDescending("createdAt");
                mainQuery.whereLessThan("createdAt", date);
                mainQuery.whereLessThanOrEqualTo("expiryDate", TimeUitls.getCurrentDate());
                mainQuery.include("from");
                mainQuery.include("object");
                mainQuery.setLimit(20);
                mainQuery.setSkip(skip);
                mainQuery.whereExists("from");

                // relations queyr
                ParseQuery queryR = ParseQuery.getQuery("UserRelations");
                queryR.whereEqualTo("user", ParseUser.getCurrentUser());
                queryR.fromLocalDatastore();

                try {

                    ParseObject relation = queryR.getFirst();

                    if (relation != null) {
                        ParseRelation<ParseObject> relationLikes = relation.getRelation("likes");
                        RxLove = relationLikes.getQuery().find();
                        ParseRelation<ParseObject> relationThumbs = relation.getRelation("thumbsUps");
                        RxThumbs = relationThumbs.getQuery().find();
                        ParseRelation<ParseObject> Relationfav = relation.getRelation("favorites");
                        RxStars = Relationfav.getQuery().find();
                    }

                    data = mainQuery.find();
                    if (skip == 0) {
                        ParseObject.unpinAll("feed");
                        ParseObject.pinAll("feed", data);

                    }
                    for (ParseObject m : data
                            ) {
                        Date now = TimeUitls.getCurrentDate();
                        Boolean isShared = false;
                        ModelFeedItem item = new ModelFeedItem();

                        ParseObject dataItem = m.getParseObject("object");
                        // set name,avatar,date
                        item.setName(m.getParseUser("from").getUsername());
                        item.setAvatar(m.getParseUser("from").getParseFile("avatar").getUrl());
                        item.setDate(dataItem.getDate("createdAt"));
                        item.setDateToString(TimeUitls.getRelativeTime(item.getDate()));

                        // set if owned
                        if (m.getParseUser("from").getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                            item.setOwnedByUser(true);
                        } else
                            item.setOwnedByUser(false);


                        dataItem.fetch();
                        // set if shared // set if owned by user
                        if (m.getInt("type") == 3) {
                            dataItem = dataItem.getParseObject("object"); // override object here
                            item.setShared(true);

                        } else {
                            item.setShared(false);
                        }

                        // set gossip details
                        if (dataItem.getClassName().equals("Gossip")) {

                            item.setGpTitle(dataItem.getString("title"));
                            item.setReactions(dataItem.getInt("reactions"));
                            item.setGpChatId(dataItem.getInt("chatId"));
                            item.setGpElapseTime(dataItem.getDate("expiryDate"));
                            item.setGpElapseTimeToString(TimeUitls.elapseTime(dataItem.getDate("expiryDate")));
                            if (RxThumbs.contains(dataItem)) {
                                item.setGpIsThumbsUp(true);
                            } else {
                                item.setGpIsThumbsUp(true);
                            }
                            item.setType(ModelFeedItem.TYPP_GOSSIP);


                        } else {

                            item.setHashtag(dataItem.getString("hashtag"));
                            item.setComment(dataItem.getInt("comments"));
                            item.setReactions(dataItem.getInt("reactions"));
                            item.setImage(dataItem.getParseFile("image").getUrl());

                            if (dataItem.getClassName().equals("Events")) {
                                item.setEvTitle(dataItem.getString("title"));
                                item.setEvLocation(dataItem.getString("address"));
                                item.setStatus(TimeUitls.getLiveBadgeText(dataItem.getDate("date")));
                                item.setEvTime(dataItem.getDate("time"));
                                item.setEvTimeToString(TimeUitls.Format12_34(dataItem.getDate("time")));
                                item.setEvDate(dataItem.getDate("date"));
                                item.setEvDateToString(TimeUitls.Format12Dec(dataItem.getDate("date")));
                                item.setEvLocation(dataItem.getString("location"));
                                item.setType(ModelFeedItem.TYPP_EVENT);
                                if (RxStars.contains(dataItem)) {
                                    item.setEvIsInterest(true);
                                } else {
                                    item.setEvIsInterest(true);
                                }

                                // // TODO: 12/17/2016  price

                            } else if (dataItem.getClassName().equals("media")) {
                                if (RxLove.contains(dataItem)) {
                                    item.setPpIsLike(true);
                                } else {
                                    item.setPpIsLike(true);
                                }

                                if (dataItem.getInt("type") == 1) {
                                    item.setPpVideo(1);
                                    item.setUrl(dataItem.getString("url"));
                                    item.setType(ModelFeedItem.TYPP_MEDIA_VIDEO);

                                } else {
                                    item.setPpVideo(0);
                                    item.setType(ModelFeedItem.TYPP_MEDIA_IMAGE);

                                }
                            }

                        }


                        returnData.add(item);
                    }

                    subscriber.onNext(returnData);
                } catch (ParseException e) {
                    e.printStackTrace();

                    subscriber.onError(e);
                } finally {
                    subscriber.onCompleted();
                }

            }
        }).subscribeOn(Schedulers.io());
    }
}
