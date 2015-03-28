package com.lody.plugin.control;

import android.app.Activity;
import android.app.Application;
import android.app.FragmentManager;
import android.app.Instrumentation;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;

import com.lody.plugin.reflect.Reflect;
import com.lody.plugin.reflect.ReflectException;

/**
 * Created by lody  on 2015/3/26.
 * <p/>
 * 插件的控制器<br>
 * 派发插件事件和生命周期
 */
public class PluginActivityControl implements PluginActivityCaller {

    Activity proxy;
    Activity plugin;
    Reflect proxyRef;
    Reflect pluginRef;
    Application app;



    public PluginActivityControl(Activity proxy, Activity plugin) {

        this(proxy, plugin,null);

    }

    public PluginActivityControl(Activity proxy, Activity plugin,Application app) {
        this.proxy = proxy;
        this.plugin = plugin;
        this.app = app;

        proxyRef = Reflect.on(proxy);
        pluginRef = Reflect.on(plugin);

    }

    public void dispatchProxyToPlugin() {
        try {
            //开始伪装插件为实体Activity
            pluginRef.set("mBase", proxy);
            pluginRef.set("mDecor", proxyRef.get("mDecor"));
            pluginRef.set("mTitleColor", proxyRef.get("mTitleColor"));
            pluginRef.set("mWindowManager", proxyRef.get("mWindowManager"));
            pluginRef.set("mWindow", proxy.getWindow());
            pluginRef.set("mManagedDialogs", proxyRef.get("mManagedDialogs"));
            pluginRef.set("mCurrentConfig", proxyRef.get("mCurrentConfig"));
            pluginRef.set("mSearchManager", proxyRef.get("mSearchManager"));
            pluginRef.set("mMenuInflater", proxyRef.get("mMenuInflater"));
            pluginRef.set("mConfigChangeFlags", proxyRef.get("mConfigChangeFlags"));
            pluginRef.set("mIntent", proxyRef.get("mIntent"));
            pluginRef.set("mToken", proxyRef.get("mToken"));
            Instrumentation instrumentation = proxyRef.get("mInstrumentation");

            pluginRef.set("mInstrumentation", new LPluginInsrument(instrumentation));
            pluginRef.set("mMainThread", proxyRef.get("mMainThread"));
            pluginRef.set("mEmbeddedID", proxyRef.get("mEmbeddedID"));
            pluginRef.set("mApplication",app == null ? proxy.getApplication() : app);
            pluginRef.set("mComponent", proxyRef.get("mComponent"));
            pluginRef.set("mActivityInfo", proxyRef.get("mActivityInfo"));
            pluginRef.set("mAllLoaderManagers", proxyRef.get("mAllLoaderManagers"));
            pluginRef.set("mLoaderManager", proxyRef.get("mLoaderManager"));
            if (Build.VERSION.SDK_INT >= 13) {
                //在android 3.2 以后，Android引入了Fragment.
                FragmentManager mFragments = proxy.getFragmentManager();
                pluginRef.set("mFragments", mFragments);
                pluginRef.set("mContainer", proxyRef.get("mContainer"));
            }
            if (Build.VERSION.SDK_INT >= 12) {
                //在android 3.0 以后，Android引入了ActionBar.
                pluginRef.set("mActionBar", proxyRef.get("mActionBar"));
            }

            pluginRef.set("mUiThread", proxyRef.get("mUiThread"));
            pluginRef.set("mHandler", proxyRef.get("mHandler"));
            pluginRef.set("mInstanceTracker", proxyRef.get("mInstanceTracker"));
            pluginRef.set("mTitle", proxyRef.get("mTitle"));
            pluginRef.set("mResultData", proxyRef.get("mResultData"));
            pluginRef.set("mDefaultKeySsb", proxyRef.get("mDefaultKeySsb"));

            plugin.getWindow().setCallback(plugin);
        /*  activityEditor.set("mInstanceTracker", thisEditor.get("mInstanceTracker"));
            activityEditor.set("mInstanceTracker", thisEditor.get("mInstanceTracker"));
            activityEditor.set("mInstanceTracker", thisEditor.get("mInstanceTracker"));*/

        } catch (ReflectException e) {
            e.printStackTrace();
        }

    }


    public Activity getPlugin() {
        return plugin;
    }

    public void setPlugin(Activity plugin) {
        this.plugin = plugin;
    }

    public Activity getProxy() {
        return proxy;
    }

    public void setProxy(Activity proxy) {
        this.proxy = proxy;
    }

    public Reflect getProxyRef() {
        return proxyRef;
    }

    public void setProxyRef(Reflect proxyRef) {
        this.proxyRef = proxyRef;
    }

    public Reflect getPluginRef() {
        return pluginRef;
    }

    public void setPluginRef(Reflect pluginRef) {
        this.pluginRef = pluginRef;
    }


    @Override
    public void callOnCreate(Bundle saveInstance) {
        getPluginRef().call("onCreate", saveInstance);
    }

    @Override
    public void callOnStart() {
        getPluginRef().call("onStart");
    }

    @Override
    public void callOnResume() {
        getPluginRef().call("onResume");
    }

    @Override
    public void callOnDestroy() {
        getPluginRef().call("onDestroy");
    }



    @Override
    public void callOnStop() {
        getPluginRef().call("onStop");
    }

    @Override
    public void callOnRestart() {
        getPluginRef().call("onRestart");
    }

    @Override
    public void callOnSaveInstanceState(Bundle outState) {
        getPluginRef().call("onSaveInstanceState", outState);
    }

    @Override
    public void callOnRestoreInstanceState(Bundle savedInstanceState) {
        getPluginRef().call("onRestoreInstanceState", savedInstanceState);
    }

    @Override
    public void callOnConfigurationChanged() {
        getPluginRef().call("onConfigurationChanged");
    }

    @Override
    public void callOnPause() {
        getPluginRef().call("onStop");
    }

    @Override
    public void callOnBackPressed() {
        getPluginRef().call("onBackPressed");
    }

    @Override
    public boolean callOnKeyDown(int keyCode, KeyEvent event) {
        return getPluginRef().call("onKeyDown",keyCode,event).get();
    }

}
