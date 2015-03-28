package com.lody.plugin;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

import com.lody.plugin.bean.LPlugin;
import com.lody.plugin.control.PluginActivityControl;
import com.lody.plugin.exception.NotFoundPluginException;
import com.lody.plugin.exception.PluginCreateFailedException;
import com.lody.plugin.exception.PluginNotExistException;
import com.lody.plugin.reflect.Reflect;

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
            Toast.makeText(this,remotePlugin.getCurrentPluginActivity()+"",Toast.LENGTH_SHORT).show();


        }

        if(!remotePlugin.isPluginInit()){
            throw new PluginCreateFailedException("Create Plugin failed!");
        }

        PluginActivityControl control = new PluginActivityControl(LActivityProxy.this,remotePlugin.getCurrentPluginActivity());
        remotePlugin.setControl(control);
        control.dispatchProxyToPlugin();
        Reflect.on( remotePlugin.getCurrentPluginActivity()).call("attachBaseContext",LActivityProxy.this);

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
}
