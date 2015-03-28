package com.lody.plugin;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.Toast;

import com.lody.plugin.bean.LPlugin;
import com.lody.plugin.control.PluginActivityCaller;
import com.lody.plugin.control.PluginActivityControl;
import com.lody.plugin.exception.LaunchPluginException;
import com.lody.plugin.exception.NotFoundPluginException;
import com.lody.plugin.exception.PluginCreateFailedException;
import com.lody.plugin.exception.PluginNotExistException;
import com.lody.plugin.reflect.Reflect;
import com.lody.plugin.service.LServiceProxy;

import java.io.File;

/**
 * Created by lody  on 2015/3/27.
 */
public class LActivityProxy extends Activity implements LProxy {

    public LPlugin remotePlugin;

    @Override
    public LPlugin loadPlugin(Activity ctx, String apkPath ) {
        //插件必须要确认有没有经过初始化，不然只是空壳

        return loadPlugin(ctx,apkPath,true);

    }

    @Override
    public LPlugin loadPlugin(Activity ctx, String apkPath, boolean checkInit) {
        LPlugin plugin = LPluginManager.loadPlugin(ctx,apkPath);


        if(checkInit) {
            if (!plugin.isPluginInit()) {
                fillPlugin(plugin);
            }
        }
        return plugin;
    }

    @Override
    public LPlugin loadPlugin(Activity ctx, String apkPath, String activityName) {
        LPlugin plugin = loadPlugin(ctx,apkPath,false);
        plugin.setTopActivityName(activityName);
        fillPlugin(plugin);
        return plugin;
    }

    @Override
    public LPlugin loadPlugin(Activity ctx, String apkPath, int index) {
        LPlugin plugin = loadPlugin(ctx,apkPath,false);
        if(plugin.isPluginInit()){
            plugin.setTopActivityName(plugin.getActivityInfos()[index].name);
            fillPlugin(plugin);
        }else{
            try {
                PackageInfo info = LPluginTool.getAppInfo(this,apkPath);
                String name = info.activities[index].name;
                plugin.setTopActivityName(name);
                fillPlugin(plugin);
            } catch (PackageManager.NameNotFoundException e) {
                throw new PluginNotExistException();
            }

        }

        return plugin;
    }

    /**
     * 装载插件
     * @param plugin
     */
    @Override
    public void fillPlugin(LPlugin plugin) {
        if(plugin == null){
            throw new PluginNotExistException();
        }
        String apkPath = plugin.getPluginPath();
        File apk = new File(apkPath);
        if(!apk.exists()) throw new NotFoundPluginException(apkPath);
        apk = null;
        fillPluginRes(plugin);
        if(!plugin.isOver()) {
            fillPluginInfo(plugin);
        }
        fillPluginLoader(plugin);
        fillPluginApplication(plugin);



    }

    private void fillPluginApplication(LPlugin plugin) {
        String appName = plugin.getAppName();
        if(appName == null)return;
        if(appName.isEmpty()) return;

        ClassLoader loader = plugin.getPluginLoader();
        if(loader == null) throw new PluginCreateFailedException();
        try {
            Application pluginApp = (Application) loader.loadClass(appName).newInstance();
            Reflect.on(pluginApp).call("attach",getApplicationContext());
            plugin.bindPluginApp(pluginApp);

        } catch (InstantiationException e) {
            throw new PluginCreateFailedException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new PluginCreateFailedException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new PluginCreateFailedException(e.getMessage());
        }


    }

    /**
     * 装载插件加载器
     * @param plugin
     */
    private void fillPluginLoader(LPlugin plugin) {


            LPluginDexLoader loader = LPluginDexLoader.getClassLoader(plugin.getPluginPath(), LActivityProxy.this, getClassLoader());
            plugin.setPluginLoader(loader);

        String top = plugin.getTopActivityName();
        if(top == null){
            top = plugin.getActivityInfos()[0].name;
            plugin.setTopActivityName(top);
        }
        try {
            Activity myPlugin = (Activity)plugin.getPluginLoader().loadClass(plugin.getTopActivityName()).newInstance();
            plugin.setCurrentPluginActivity(myPlugin);

        } catch (Exception e) {
            throw new LaunchPluginException(e.getMessage());
        }
    }

    /**
     * 注册插件信息
     * @param plugin
     */
    private void fillPluginInfo(LPlugin plugin) {
        PackageInfo info = null;
        try {
            info = LPluginTool.getAppInfo(LActivityProxy.this, plugin.getPluginPath());
        } catch (PackageManager.NameNotFoundException e) {
            throw new PluginNotExistException(plugin.getPluginPath());
        }
        if (info == null){
            throw new PluginCreateFailedException("Can't create Plugin from :" + plugin.getPluginPath());
        }
        plugin.setPluginPkgInfo(info);
        plugin.setAppName(info.applicationInfo.className);
        plugin.setOver(true);

    }

    /**
     * 装载插件资源
     * @param plugin
     */
    private void fillPluginRes(LPlugin plugin) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Reflect assetRef = Reflect.on(assetManager);
            assetRef.call("addAssetPath", plugin.getPluginPath());
            plugin.setPluginAssetManager(assetManager);
            Resources superRes = super.getResources();
            Resources pluginRes = new Resources(assetManager,
                    superRes.getDisplayMetrics(),
                    superRes.getConfiguration());
            plugin.setPluginRes(pluginRes);
            Resources.Theme pluginTheme = plugin.getPluginRes().newTheme();
            pluginTheme.setTo(super.getTheme());
            plugin.setCurrentPluginTheme(pluginTheme);

        } catch (Exception e){

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread,final Throwable ex) {

                ex.printStackTrace();

                final Context context = Reflect.on("android.app.ActivityThread").call("currentActivityThread").call("getSystemContext").get();
                new Thread() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Toast.makeText(context, "Sorry,the Plugin maybe met some error,so it will be crash:(\n", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }.start();
                try {
                    Thread.sleep(2600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Thread.setDefaultUncaughtExceptionHandler(Thread.getDefaultUncaughtExceptionHandler());

                android.os.Process.killProcess(android.os.Process.myPid());

                System.exit(1);


            }
        });
        super.onCreate(savedInstanceState);
        final Bundle pluginMessage = getIntent().getExtras();
        String pluginActivityName;
        String pluginDexPath;
        //int pluginIndex;
        if(pluginMessage != null){
            pluginActivityName = pluginMessage.getString(LPluginConfig.KEY_PLUGIN_ACT_NAME, LPluginConfig.DEF_PLUGIN_CLASS_NAME);
            pluginDexPath = pluginMessage.getString(LPluginConfig.KEY_PLUGIN_DEX_PATH, LPluginConfig.DEF_PLUGIN_DEX_PATH);
            //pluginIndex = pluginMessage.getInt(LPluginConfig.KEY_PLUGIN_INDEX, 0);
        }else{
            throw new PluginCreateFailedException("Please put the Plugin Path!");
        }
        if(pluginDexPath == LPluginConfig.DEF_PLUGIN_DEX_PATH){
            throw new PluginCreateFailedException("Please put the Plugin Path!");
        }
            remotePlugin = loadPlugin(LActivityProxy.this, pluginDexPath,false);
            if(pluginActivityName != LPluginConfig.DEF_PLUGIN_CLASS_NAME){
                remotePlugin.setTopActivityName(pluginActivityName);
            }

        if(!remotePlugin.isPluginInit()){
            fillPlugin(remotePlugin);
        }

        if(!remotePlugin.isPluginInit()){
            throw new PluginCreateFailedException("Create Plugin failed!");
        }

        //Toast.makeText(this, remotePlugin.getPluginApp()+"", Toast.LENGTH_SHORT).show();


        PluginActivityControl control = new PluginActivityControl(LActivityProxy.this,remotePlugin.getCurrentPluginActivity(),remotePlugin.getPluginApp());

        remotePlugin.setControl(control);
        control.dispatchProxyToPlugin();
        Reflect.on( remotePlugin.getCurrentPluginActivity()).call("attachBaseContext",LActivityProxy.this);
        setTitle(remotePlugin.getPluginPkgInfo().applicationInfo.loadLabel(getPackageManager()));
        control.callOnCreate(savedInstanceState);

    }





    @Override
    public Resources getResources() {
        if(remotePlugin == null)
            return super.getResources();
        return remotePlugin.getPluginRes() == null?super.getResources():remotePlugin.getPluginRes();
    }

    @Override
    public Resources.Theme getTheme() {
        if(remotePlugin == null)
            return super.getTheme();
        return  remotePlugin.getCurrentPluginTheme() == null? super.getTheme() : remotePlugin.getCurrentPluginTheme();
    }

    @Override
    public AssetManager getAssets() {
        if(remotePlugin == null)
        return super.getAssets();
        return remotePlugin.getPluginAssetManager() == null ? super.getAssets() : remotePlugin.getPluginAssetManager();
    }


    @Override
    public ClassLoader getClassLoader() {
        if(remotePlugin == null){
            return super.getClassLoader();
        }
        if(remotePlugin.isPluginInit())
        {
            return remotePlugin.getPluginLoader();
        }
        return super.getClassLoader();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(remotePlugin == null){
            return;
        }
        PluginActivityCaller caller = remotePlugin.getControl();
        if(caller != null){
            caller.callOnResume();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(remotePlugin == null){
            return;
        }
        PluginActivityCaller caller = remotePlugin.getControl();
        if(caller != null){
            caller.callOnStop();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(remotePlugin == null){
            return;
        }
        PluginActivityCaller caller = remotePlugin.getControl();
        if(caller != null){
            caller.callOnDestroy();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if(remotePlugin == null){
            return;
        }
        PluginActivityCaller caller = remotePlugin.getControl();
        if(caller != null){
            caller.callOnPause();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(remotePlugin == null){
            return;
        }
        PluginActivityCaller caller = remotePlugin.getControl();
        if(caller != null){
            caller.callOnSaveInstanceState(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(remotePlugin == null){
            return;
        }
        PluginActivityCaller caller = remotePlugin.getControl();
        if(caller != null){
            caller.callOnRestoreInstanceState(savedInstanceState);
        }
        getFragmentManager();
    }

    @Override
    public void onBackPressed() {

        if(remotePlugin == null){
            super.onBackPressed();
        }
        PluginActivityCaller caller = remotePlugin.getControl();
        if(caller != null){
            caller.callOnBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(remotePlugin == null){
            return;
        }
        PluginActivityCaller caller = remotePlugin.getControl();
        if(caller != null){
            caller.callOnStop();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(remotePlugin == null){
            return;
        }
        PluginActivityCaller caller = remotePlugin.getControl();
        if(caller != null){
            caller.callOnRestart();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(remotePlugin == null){
            return super.onKeyDown(keyCode, event);
        }
        PluginActivityCaller caller = remotePlugin.getControl();
        if(caller != null){
            return caller.callOnKeyDown(keyCode,event);
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public ComponentName startService(Intent service) {
        Intent i = new Intent(this, LServiceProxy.class);
       Toast.makeText(this,"Sorry,Direct-Load-Apk is not support Service yet:) \nIt well support in the near!",Toast.LENGTH_SHORT).show();
        return i.getComponent();
    }


}
