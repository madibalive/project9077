package com.venu.venutheta.utils;

import net.hockeyapp.android.CrashManagerListener;

/**
 * Created by Madiba on 12/21/2016.
 */

public class CustomCrashManagerListener extends CrashManagerListener {
    @Override
    public boolean shouldAutoUploadCrashes() {
        return true;
    }
}
