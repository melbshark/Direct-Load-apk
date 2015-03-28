package com.lody.plugin;

import android.content.Context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dalvik.system.DexClassLoader;

/**
 * Created by lody  on 2015/3/24.
 * 插件的核心加载器
 */
public class LPluginDexLoader extends DexClassLoader {

    private static final Map<String, LPluginDexLoader> pluginLoader = new ConcurrentHashMap<String, LPluginDexLoader>();

    protected LPluginDexLoader(String dexPath, String optimizedDirectory,
                               String libraryPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, libraryPath, parent);
    }

    /**
     * 返回apk对应的加载器，不会重复加载同样的资源
     */
    public static LPluginDexLoader getClassLoader(String dexPath, Context cxt,
                                                  ClassLoader parent) {
        LPluginDexLoader pluginDexLoader = pluginLoader.get(dexPath);
        if (pluginDexLoader == null) {
            // 获取到app的启动路径
            final String dexOutputPath = cxt
                    .getDir("plugin", Context.MODE_PRIVATE).getAbsolutePath();
            final String libOutputPath = cxt
                    .getDir("plugin_lib", Context.MODE_PRIVATE).getAbsolutePath();
            pluginDexLoader = new LPluginDexLoader(dexPath, dexOutputPath, libOutputPath, parent);
            pluginLoader.put(dexPath, pluginDexLoader);
        }
        return pluginDexLoader;
    }
}
