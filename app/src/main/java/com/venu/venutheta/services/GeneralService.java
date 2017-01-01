package com.venu.venutheta.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.venu.venutheta.Actions.ActionEvantPageBuy;
import com.venu.venutheta.models.GlobalConstants;
import com.venu.venutheta.utils.NotificationUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GeneralService extends IntentService {
    private static final String TAG = GeneralService.class.getSimpleName();

    private static final String ACTION_NOTIFICATIONS = "com.example.madiba.venu_v2.action.NOTIFICATIONS";
    private static final String ACTION_SEND_PUSH_MSG = "com.example.madiba.venu_v2.action.PUSH_MSG";
    private static final String ACTION_GENERIC_ACTION = "com.example.madiba.venu_v2.action.GENERIC_ACTION";
    private static final String ACTION_SHARE = "com.example.madiba.venu_v2.action.SHARE";
    private static final String ACTION_SET_GOING = "com.example.madiba.venu_v2.actionSET_GOING";
    private static final String ACTION_CANCEL_GOING = "com.example.madiba.venu_v2.actionCANCEL_GOING";
    private static final String ACTION_UNFOLLOW = "com.example.madiba.venu_v2.action.UNFOLLOW";
    private static final String ACTION_FOLLOW = "com.example.madiba.venu_v2.action.FOLLOW";
    private static final String ACTION_CHECK_FOLLOWING = "com.example.madiba.venu_v2.action.CHECK_FOLLOWING";
    private static final String ACTION_STARTUP = "com.example.madiba.venu_v2.action.STARTUP";
    private static final String ACTION_PENDING_INVITES = "com.example.madiba.venu_v2.action.PENDING_INVITES";

    public GeneralService() {
        super("GeneralService");
    }



    public static void startActionSentNotification(Context context, String id,String classname) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(ACTION_NOTIFICATIONS);
        intent.putExtra("object_id", id);
        intent.putExtra("className", classname);
        context.startService(intent);
    }



    public static void startActionGenericAction(Context context, Boolean state, String id, String className,String relName) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(ACTION_GENERIC_ACTION);
        intent.putExtra("state", state);
        intent.putExtra("id", id);
        intent.putExtra("relName", relName);
        intent.putExtra("className", className);
        context.startService(intent);
    }

    public static void startActionPendingInvite(Context context,ArrayList<String> numbers, String id, String className,String relName) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(ACTION_PENDING_INVITES);
        intent.putStringArrayListExtra("state", numbers);
        intent.putExtra("id", id);
        intent.putExtra("relName", relName);
        intent.putExtra("className", className);
        context.startService(intent);
    }

    public static void startActionShare(Context context,  String id, String className) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(ACTION_SHARE);
        intent.putExtra("id", id);
        intent.putExtra("className", className);
        context.startService(intent);
    }

    public static void startActionFollow(Context context,  String id, String className,int type) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(ACTION_FOLLOW);
        intent.putExtra("id", id);
        intent.putExtra("type", type);

        intent.putExtra("className", className);
        context.startService(intent);
    }

    public static void startActionUnFollow(Context context,  String id, String className,int type) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(ACTION_UNFOLLOW);
        intent.putExtra("id", id);
        intent.putExtra("type", type);
        intent.putExtra("className", className);
        context.startService(intent);
    }

    public static void startActionCheckFollowing(Context context,  String id, String className,int type) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(ACTION_CHECK_FOLLOWING);
        intent.putExtra("id", id);
        intent.putExtra("type", type);
        intent.putExtra("className", className);
        context.startService(intent);
    }

    public static void startActionCancelGoing(Context context,  String id, String className) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(ACTION_CANCEL_GOING);
        intent.putExtra("id", id);
        intent.putExtra("className", className);
        context.startService(intent);
    }

    public static void startActionSetGoing(Context context,  String id, String className) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(ACTION_SET_GOING);
        intent.putExtra("id", id);
        intent.putExtra("className", className);
        context.startService(intent);
    }

    public static void startActionStartUp(Context context) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(ACTION_STARTUP);
        context.startService(intent);
    }
    public static void startActionSendPushMsg(Context context, String param1, String param2) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(ACTION_SEND_PUSH_MSG);
        intent.putExtra("recipient_id", param1);
        intent.putExtra("conversation_id", param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleIntent: started");
        if (intent != null) {
            Log.i(TAG, "onHandleIntent: " +intent.getAction());
            final String action = intent.getAction();
            if (ACTION_GENERIC_ACTION.equals(action)) {
                final String className = intent.getStringExtra("className");
                final String id = intent.getStringExtra("id");
                final String relName = intent.getStringExtra("relName");
                final Boolean state = intent.getBooleanExtra("state", false);
                handleActionGenericAction(state, id, className,relName);

            } else if (ACTION_STARTUP.equals(action)) {
                handleActionUpdateRelation();

            } else if (ACTION_SET_GOING.equals(action)) {
                final String className = intent.getStringExtra("className");
                final String id = intent.getStringExtra("id");
                handleActionSetGoing( id, className);

            } else if (ACTION_NOTIFICATIONS.equals(action)) {
                final String className = intent.getStringExtra("className");
                final String id = intent.getStringExtra("id");
                final int type = intent.getIntExtra("type",0);
                handleActionSendNotification( id, className,type);

            }  else if (ACTION_CANCEL_GOING.equals(action)) {
                final String className = intent.getStringExtra("className");
                final String id = intent.getStringExtra("id");
                handleActionCancelGoing( id, className);

            }  else if (ACTION_CHECK_FOLLOWING.equals(action)) {
                final String id = intent.getStringExtra("id");
                handleActionCheckFollowing( id);


            }  else if (ACTION_UNFOLLOW.equals(action)) {
                final String className = intent.getStringExtra("className");
                final String id = intent.getStringExtra("id");
                final int type = intent.getIntExtra("type",0);
                handleActionUnFollow( id, className,type);

            }  else if (ACTION_FOLLOW.equals(action)) {
                final String className = intent.getStringExtra("className");
                final String id = intent.getStringExtra("id");
                final int type = intent.getIntExtra("type",0);
                handleActionFollow( id, className,type);

            }  else if (ACTION_SHARE.equals(action)) {
                final String className = intent.getStringExtra("className");
                final String id = intent.getStringExtra("id");
                handleActionShare( id, className);

            } else if (ACTION_PENDING_INVITES.equals(action)) {

                final ArrayList<String> param1 = intent.getStringArrayListExtra("Recipient_Id");
                final String className = intent.getStringExtra("className");
                final String id = intent.getStringExtra("id");
                handlePendingInvites(param1, id, className);

            } else if (ACTION_SEND_PUSH_MSG.equals(action)) {

                final String param1 = intent.getStringExtra("Recipient_Id");
                final String param2 = intent.getStringExtra("Conversation_Id");
                final String message = intent.getStringExtra("message");
                handleActionPushMsg(param1, param2, message);

            }else {
                Log.i(TAG, "onHandleIntent: no action matched");
            }
        }
    }



    private void handleActionFollow(String id,String className, int type){

        if (type==1){
            ParseObject follow = new ParseObject("FollowVersion3");
            follow.put("from", ParseUser.getCurrentUser());
            follow.put("to", ParseUser.createWithoutData(ParseUser.class,id));
            follow.put("fromId", ParseUser.getCurrentUser().getObjectId());
            follow.put("toId", id);
            follow.put("type", GlobalConstants.TYPE_FOLLOW);
            try {
                follow.save();

                // send post possible
            } catch (ParseException e) {
                e.printStackTrace();

                // send post negative
            }
        }


    }

    private void handleActionUnFollow(String id,String className, int type){

        if (type==1){

            ParseQuery<ParseObject> checkQ = ParseQuery.getQuery("FollowVersion3");
            checkQ.whereEqualTo("from", ParseUser.getCurrentUser());
            checkQ.whereEqualTo("to", ParseUser.createWithoutData(ParseUser.class,id));

            try {
                ParseObject obj = checkQ.getFirst();
                if ( obj!=null){
                    obj.delete();

                    // send post
                }

            } catch (ParseException e) {
                e.printStackTrace();

                // send post error
            }
        }


    }

    private void handleActionCheckFollowing(String id){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("FollowVersion3");
        query.whereExists("from");
        query.whereEqualTo("from", ParseUser.getCurrentUser());

        try {
            if (query.getFirst() !=null){
                // send following
            }else {
                // send not following
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void handleActionCancelGoing(String id,String className){
        ParseObject event = ParseObject.createWithoutData(className,id);

        ParseQuery<ParseObject> checkQ = ParseQuery.getQuery("GoingList");
        checkQ.whereEqualTo("event",event);
        checkQ.whereEqualTo("user", ParseUser.getCurrentUser());

        try {
            ParseObject obj = checkQ.getFirst();
            if ( obj!=null){
                obj.delete();
                handleActionEventBuy(id,className);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private void handleActionSetGoing(String id,String className){
        ParseObject event = ParseObject.createWithoutData(className,id);

        ParseQuery<ParseObject> checkQ = ParseQuery.getQuery("GoingList");
        checkQ.whereEqualTo("event",event);
        checkQ.whereEqualTo("user", ParseUser.getCurrentUser());

        try {
            ParseObject obj = checkQ.getFirst();
            if ( obj ==null){
                ParseObject newObj = new ParseObject("");
                newObj.put("event",event);
                newObj.put("user", ParseUser.getCurrentUser());
                newObj.save();

                handleActionEventBuy(id,className);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private void handleActionEventBuy(String id,String className){
        ParseObject event = ParseObject.createWithoutData(className,id);
        ActionEvantPageBuy action = new ActionEvantPageBuy();

        ParseQuery<ParseObject> isInvitedCheck = ParseQuery.getQuery("");
        isInvitedCheck.whereEqualTo("objectId",event.getObjectId());
        isInvitedCheck.whereContains("inviteList", ParseUser.getCurrentUser().getObjectId());

        ParseQuery<ParseObject> checkQ = ParseQuery.getQuery("GoingList");
        checkQ.whereEqualTo("event",event);
        checkQ.whereEqualTo("user", ParseUser.getCurrentUser());

        try {
            event.fetch();
            ParseObject inviteCheck = isInvitedCheck.getFirst();
            ParseObject returnCheck =checkQ.getFirst();

            if (returnCheck != null){
                action.setGoing(true);

                if (event.getInt("type")==1)
                    action.setMsg("");
                else if (event.getInt("type")==2)
                    action.setMsg("");
                else if (event.getInt("type")==2)
                    action.setMsg("");
                else if (event.getInt("type")==2)
                    action.setMsg("");



            }else {
                action.setGoing(false);

                if (event.getInt("type")==1)
                    action.setMsg("");
                else if (event.getInt("type")==2)
                    action.setMsg("");
                else if (event.getInt("type")==2)
                    action.setMsg("");
                else if (event.getInt("type")==2)
                    action.setMsg("");
            }

            if (inviteCheck !=null){
                action.setInvited(true);


            }else {
                action.setInvited(false);
            }

            action.setError(false);
            EventBus.getDefault().post(action);


        } catch (ParseException e) {
            action.setError(true);
            EventBus.getDefault().post(action);
            e.printStackTrace();
        }



    }




    private void handleActionPushMsg(String recId, String convId, String msg) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("recipient_id", recId);
        params.put("conversation_id", convId);
        params.put("message", msg);

//        ParseCloud.callFunctionInBackground("sendPush", params, new
//                FunctionCallback<Object>() {
//                    @Override
//                    public void done(Object object, ParseException e) {
//                        if (e == null) {
//                        } else {
//
//                        }
//                    }
//                });
    }


    private void handleActionGenericAction(Boolean state, String id, String className,String relname) {
        if (id != null && className !=null) {
            ParseObject m = ParseObject.createWithoutData(className, id);
            ParseQuery query = ParseQuery.getQuery("UserRelations");
            query.whereEqualTo("user", ParseUser.getCurrentUser());
            try {
                ParseObject queryObject = query.getFirst();
                m.fetch();
                ParseObject notObj= new ParseObject("Notifications");
                notObj.put("from", ParseUser.getCurrentUser());
                notObj.put("to",m.getParseUser("from"));
                notObj.put("type",1);
                notObj.put("object",m);

                if (queryObject !=null) {
                    ParseRelation<ParseObject> relation = queryObject.getRelation(relname);
                    if (state) {
                        m.increment("reactions");
                        relation.add(m);

                        m.save();
                        notObj.save();                    }
                    else {
                        m.put("reactions",m.getInt("reactions")-1);
                        m.save();
                        relation.remove(m);
                    }
                    queryObject.save();
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    private void handleActionSendNotification( String id, String className,int type){
        ParseObject m = ParseObject.createWithoutData(className, id);
        try {
            m.fetch();
            ParseObject obj= new ParseObject("Notifications");
            obj.put("from", ParseUser.getCurrentUser());
            obj.put("to",m.getParseUser("from"));
            obj.put("toId",m.getParseUser("from").getObjectId());
            obj.put("type",type);
            obj.put("fromID", ParseUser.getCurrentUser().getObjectId());
            obj.put("object",m);
            if (type ==4){
                obj.put("inviteAction",false);
            }
            obj.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void handleActionShare( String id, String className) {
        Log.i(TAG, "handleActionLike: started id : " + id + " classname: " + className);

        if (id != null && className != null) {
            ParseObject m = ParseObject.createWithoutData(className, id);
            try {

                ParseQuery<ParseObject> shareQ = ParseQuery.getQuery("Share");
                shareQ.whereEqualTo("object", m);
                shareQ.whereEqualTo("from", ParseUser.getCurrentUser());

                ParseObject exist = shareQ.getFirst();

                if (exist == null) {
                    m.fetch();
                    m.increment("shares");
                    m.save();

                    ParseObject shareObject= new ParseObject("Share");
                    shareObject.put("from", ParseUser.getCurrentUser());
                    shareObject.put("fromID", ParseUser.getCurrentUser().getObjectId());
                    shareObject.put("object",m);
                    shareObject.save();

                    // TODO: 12/18/2016 added feed
                }
            } catch (ParseException e) {

            }

        }
    }

    private void handlePendingInvites(ArrayList<String> numbers, String id, String className){
        ParseObject m = ParseObject.createWithoutData(className, id);

        for (String number: numbers) {
            ParseObject inviteObject=new ParseObject("PendingInvites");
            inviteObject.put("from", m);
            inviteObject.put("event",m);
            inviteObject.put("toPhone",number);
            try {
                inviteObject.save();

                NotificationUtils.createNotification(getApplicationContext(),"Local Contacts sucess","",210);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // onsuncess create invites
        }
    }

    private void handleActionUpdateRelation(){

        ParseQuery relations = ParseQuery.getQuery("UserRelations");
        relations.whereEqualTo("user", ParseUser.getCurrentUser());
        try {
            ParseObject relationLikeQ = relations.getFirst();

            ParseRelation<ParseObject> relationLikes = relationLikeQ.getRelation("likes");
            List<ParseObject> likes = relationLikes.getQuery().find();
            if (likes.size()> 0){
                ParseObject.unpinAll("USER_LIKES");
                ParseObject.pinAll("USER_LIKES",likes);
            }

            ParseRelation<ParseObject> relationFav = relationLikeQ.getRelation("favourite");
            List<ParseObject> favs = relationFav.getQuery().find();
            if (favs.size()> 0){
                ParseObject.unpinAll("USER_FAVORITES");
                ParseObject.pinAll("USER_FAVORITES",favs);
            }

            ParseRelation<ParseObject> relationThumbs = relationLikeQ.getRelation("thumbsup");
            List<ParseObject> thumbs = relationThumbs.getQuery().find();
            if (thumbs.size()> 0){
                ParseObject.unpinAll("USERS_THUMBS_UP");
                ParseObject.pinAll("USERS_THUMBS_UP",thumbs);
            }

        } catch (ParseException e) {
            e.printStackTrace();

        }

    }

}
