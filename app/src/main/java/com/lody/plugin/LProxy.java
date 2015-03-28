package com.lody.plugin;

import android.app.Activity;

import com.lody.plugin.bean.LPlugin;

/**
 * Created by lody  on 2015/3/27.
 */
public interface LProxy {
    /**
     * 加载一个插件
     * @param proxyParent
     * @param apkPath
     * @return
     */
    LPlugin loadPlugin(Activity proxyParent,String apkPath);
    LPlugin loadPlugin(Activity proxyParent,String apkPath ,boolean checkInit);
    LPlugin loadPlugin(Activity proxyParent,String apkPath,String activityName);
    LPlugin loadPlugin(Activity proxyParent,String apkPath,int index);
    void fillPlugin(LPlugin plugin);

}
