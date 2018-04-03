package com.mercandalli.android.apps.theremin.main;

import android.app.Application;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MainGraph.init(this);
    }
}
