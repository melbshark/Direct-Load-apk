package com.lody.plugin.control;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.lody.plugin.LActivityProxy;
import com.lody.plugin.LPluginConfig;
import com.lody.plugin.LPluginManager;
import com.lody.plugin.reflect.Reflect;

/**
 * Created by lody  on 2015/3/27.
 *
 * @author Lody
 *
 * 负责转移插件的跳转目标<br>
 * @see android.app.Activity#startActivity(android.content.Intent)
 */
public class LPluginInsrument extends Instrumentation {
    Instrumentation pluginIn;
    Reflect instrumentRef;
    public LPluginInsrument(Instrumentation pluginIn){
        this.pluginIn = pluginIn;
        instrumentRef = Reflect.on(pluginIn);
    }

    /**@Override*/
    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {

        Intent gotoPluginOrHost = new Intent();
        ComponentName componentName = intent.getComponent();
        String className = componentName.getClassName();
        String packageName = componentName.getPackageName();

        //Toast.makeText(who,  className, Toast.LENGTH_LONG).show();
        gotoPluginOrHost.setClass(who, LActivityProxy.class);
        gotoPluginOrHost.putExtra(LPluginConfig.KEY_PLUGIN_DEX_PATH, LPluginManager.finalApkPath);
        gotoPluginOrHost.putExtra(LPluginConfig.KEY_PLUGIN_ACT_NAME,className);
        ActivityResult result =instrumentRef.call("execStartActivity",who,contextThread,token,target,gotoPluginOrHost,requestCode,options).get();
      return result;
    }

}
