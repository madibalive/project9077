package com.venu.venutheta;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

import timber.log.Timber;


/**
 * Created by Madiba on 12/1/2016.
 */

public class Application extends android.app.Application  {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());
//        HockeyLog.setLogLevel(Log.ERROR); // show only errors – the default log level
//        AdobeCSDKFoundation.initializeCSDKFoundation(getApplicationContext());

        Parse.enableLocalDatastore(this);
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId(BuildConfig.PARSE_ID)
                .clientKey(BuildConfig.PARSE_KEY)
                .server(BuildConfig.PARSE_URL)
                .build());
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(this);


    }

//    private HttpProxyCacheServer newProxy() {
//        return new HttpProxyCacheServer(this);
//    }
//
//    @Override
//    public String getClientID() {
//        return BuildConfig.ADOBE_KEY;
//    }
//
//    @Override
//    public String getClientSecret() {
//        return BuildConfig.ADOBE_SECRET;
//    }
//
//    @Override
//    public String getRedirectUri() {
//        return BuildConfig.ADOBE_URL;
//    }

}
