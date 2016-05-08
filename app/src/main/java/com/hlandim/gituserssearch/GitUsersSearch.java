package com.hlandim.gituserssearch;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by hlandim on 5/7/16.
 */
public class GitUsersSearch extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        configureFaceBoock();
    }

    private void configureFaceBoock() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
}
