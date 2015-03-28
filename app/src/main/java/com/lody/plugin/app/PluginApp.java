package com.lody.plugin.app;

import android.app.Application;
import android.content.res.Configuration;
import android.os.Build;

/**
 * Created by lody  on 2015/3/28.
 */
public class PluginApp extends Application {

    Application pluginApp;

    public PluginApp(Application pluginApp){
        this.pluginApp = pluginApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pluginApp.onCreate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        pluginApp.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        pluginApp.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        pluginApp.onConfigurationChanged(newConfig);
    }

    /**@Override*/
    public void onTrimMemory(int level) {
        if(Build.VERSION.SDK_INT > 14) {
            super.onTrimMemory(level);
            pluginApp.onTrimMemory(level);
        }
    }

}
