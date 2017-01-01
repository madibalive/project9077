package com.venu.venutheta.utils;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

/**
 * Created by Madiba on 12/29/2016.
 */
public class ProxyFactory {

    private static HttpProxyCacheServer sharedProxy;

    private ProxyFactory() {
    }

    public static HttpProxyCacheServer getProxy(Context context) {
        return sharedProxy == null ? (sharedProxy = newProxy(context)) : sharedProxy;
    }

    private static HttpProxyCacheServer newProxy(Context context) {
        return new HttpProxyCacheServer(context);
    }
}
