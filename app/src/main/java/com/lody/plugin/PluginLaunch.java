package com.lody.plugin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by lody  on 2015/3/24.
 */
public class PluginLaunch {

    /**
     * 直接启动一个apk
     *
     * @param context    当前上下文
     * @param pluginPath 插件路径
     */
    public static void startPlugin(Context context, String pluginPath) {
        Intent i = new Intent(context, LActivityProxy.class);
        Bundle bundle = new Bundle();
        bundle.putString(LPluginConfig.KEY_PLUGIN_DEX_PATH, pluginPath);
        i.putExtras(bundle);
        context.startActivity(i);
    }

    /**
     * 直接启动一个apk
     *
     * @param context
     * @param pluginPath
     * @param index      插件中的第几个Activity？
     */
    public static void startPlugin(Context context, String pluginPath, int index) {
        Intent i = new Intent(context, LActivityProxy.class);
        Bundle bundle = new Bundle();
        bundle.putString(LPluginConfig.KEY_PLUGIN_DEX_PATH, pluginPath);
        i.putExtras(bundle);
        context.startActivity(i);
    }
}
