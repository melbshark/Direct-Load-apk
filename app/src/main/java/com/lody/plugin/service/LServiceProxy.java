package com.lody.plugin.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by lody  on 2015/3/28.
 */
public class LServiceProxy extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
