package com.lody.plugin.bean;

import android.app.Activity;
import android.app.Application;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.lody.plugin.control.PluginActivityControl;

import java.util.List;

/**
 * Created by lody  on 2015/3/27.
 */
public class LPlugin {
    /** 插件的Application */
    Application pluginApp;
    /** 插件路径 */
    String pluginPath;
    /** 插件资源管理器 */
    AssetManager pluginAssetManager;
    /** 插件资源 */
    Resources pluginRes;
    /** 插件主题 */
    Resources.Theme currentPluginTheme;
    /** 插件Activity信息 */
    ActivityInfo activityInfos[];
    /** 插件包信息 */
    PackageInfo pluginPkgInfo;
    /** 插件代理器 */
    Activity proxyParent;
    /** 插件实体Activity */
    Activity CurrentPluginActivity;
    /** 插件加载器 */
    ClassLoader pluginLoader;
    /** 插件是否已经完成初始化 */
    boolean isPluginInit;
    /**插件的Action过滤器 */
    List<IntentFilter> pluginFilters;
    /** 插件的第一个Activity */
    String topActivityName = null;
    /** 插件控制器 */
    PluginActivityControl control;
    /** 是否需要重复加载 */
    boolean isOver = false;


    /** 插件的Application名 */
    String appName;

    public boolean isOver() {
        return isOver;
    }

    public void setOver(boolean isOver) {
        this.isOver = isOver;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }


    /**
     * @return 插件的Application
     */
    public Application getPluginApp() {
        return pluginApp;
    }

    /**
     * 绑定插件的Application
     * @param pluginApp
     */
    public void bindPluginApp(Application pluginApp) {
        this.pluginApp = pluginApp;
    }

    public void setPluginFilters(List<IntentFilter> pluginFilters) {
        this.pluginFilters = pluginFilters;
    }

    public String getTopActivityName() {
        return topActivityName;
    }

    public void setTopActivityName(String topActivityName) {
        this.topActivityName = topActivityName;
    }

    public PluginActivityControl getControl() {
        return control;
    }

    public void setControl(PluginActivityControl control) {
        this.control = control;
    }


    public LPlugin(Activity proxyParent,String apkPath){

        this.proxyParent = proxyParent;
        this.pluginPath = apkPath;

    }

    /**
     * 得到代理的实体
     * @return
     */
    public Activity getProxyParent() {
        return proxyParent;
    }

    public List<IntentFilter> getPluginFilters() {
        return pluginFilters;
    }

    /**
     * 设置代理的实体
     * @param proxyParent
     */
    public void setProxyParent(Activity proxyParent) {
        this.proxyParent = proxyParent;
    }

    public String getPluginPath() {
        return pluginPath;
    }

    public void setPluginPath(String pluginPath) {
        this.pluginPath = pluginPath;
    }

    public AssetManager getPluginAssetManager() {
        return pluginAssetManager;
    }

    public void setPluginAssetManager(AssetManager pluginAssetManager) {
        this.pluginAssetManager = pluginAssetManager;
    }

    public Resources getPluginRes() {
        return pluginRes;
    }

    public void setPluginRes(Resources pluginRes) {
        this.pluginRes = pluginRes;
    }

    public Resources.Theme getCurrentPluginTheme() {
        return currentPluginTheme;
    }

    public void setCurrentPluginTheme(Resources.Theme currentPluginTheme) {
        this.currentPluginTheme = currentPluginTheme;
    }

    public ActivityInfo[] getActivityInfos() {
        return activityInfos;
    }

    public void setActivityInfos(ActivityInfo[] activityInfos) {
        this.activityInfos = activityInfos;
    }

    public PackageInfo getPluginPkgInfo() {
        return pluginPkgInfo;
    }

    public void setPluginPkgInfo(PackageInfo pluginPkgInfo) {
        this.pluginPkgInfo = pluginPkgInfo;
        this.activityInfos = pluginPkgInfo.activities;
    }

    public Activity getCurrentPluginActivity() {
        return CurrentPluginActivity;
    }

    public void setCurrentPluginActivity(Activity currentPluginActivity) {
        CurrentPluginActivity = currentPluginActivity;
    }

    public ClassLoader getPluginLoader() {
        return pluginLoader;
    }

    public void setPluginLoader(ClassLoader pluginLoader) {
        this.pluginLoader = pluginLoader;
    }

    public boolean isPluginInit() {
        isPluginInit = activityInfos != null;
        isPluginInit = getCurrentPluginActivity() != null;
        return isPluginInit;
    }





}
