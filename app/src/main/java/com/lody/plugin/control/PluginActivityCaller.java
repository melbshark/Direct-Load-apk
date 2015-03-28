package com.lody.plugin.control;

import android.os.Bundle;

/**
 * Created by lody  on 2015/3/26.
 */
public interface PluginActivityCaller {
    void callOnCreate(Bundle saveInstance);

    void callOnStart();

    void callOnResume();

    void callOnDestroy();

    void callOnStop();

    void callOnRestart();

    void callOnSaveInstanceState(Bundle outState);

    void callOnRestoreInstanceState(Bundle savedInstanceState);

    void callOnConfigurationChanged();

}
