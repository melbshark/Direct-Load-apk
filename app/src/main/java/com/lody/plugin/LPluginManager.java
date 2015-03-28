package com.lody.plugin;

import android.app.Activity;

import com.lody.plugin.bean.LPlugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lody  on 2015/3/27.
 */
public class LPluginManager{

    private static final Map<String,LPlugin> pluginsMapForPath = new ConcurrentHashMap<String, LPlugin>();
    public static String finalApkPath;

    public static LPlugin loadPlugin(Activity proxyParent,String apkPath){
        finalApkPath = apkPath;
        LPlugin plugin = null;
        plugin = pluginsMapForPath.get(apkPath);
        if(plugin == null){
            plugin = new LPlugin(proxyParent,apkPath);
        }
        return plugin;
    }



}
