package com.noob.skin;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;
import android.widget.ImageView;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * Created by xiaoqi on 2018/8/31
 */
public class ResourceLoad {

    public static void loadInstallApkRes(Context ctx, ImageView imageView){
        //加载已安卓app应用的资源
        try {
            //创建已安装app的context
            Context context = ctx.getApplicationContext().createPackageContext("com.noob.resourcesapp", Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            Resources resources = context.getResources();
            //反射获取R.class
            Class aClass = context.getClassLoader().loadClass("com.noob.resourcesapp.R$drawable");
            int id = (int) aClass.getField("icon_collect").get(null);
            imageView.setImageDrawable(resources.getDrawable(id));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void loadUnInstallApkRes(Context ctx, ImageView imageView){
        try {
            String apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.apk";
            //获取未安装apk的AssetManager
            AssetManager assetManager = AssetManager.class.newInstance();
            Method method = assetManager.getClass().getMethod("addAssetPath", String.class);
            method.invoke(assetManager, apkPath);
            File dexDir = ctx.getDir("dex", Context.MODE_PRIVATE);
            if (!dexDir.exists()) {
                dexDir.mkdir();
            }
            //获取未安装apk的Resources
            Resources resources = new Resources(assetManager, ctx.getResources().getDisplayMetrics(),
                    ctx.getResources().getConfiguration());
            //获取未安装apk的ClassLoader
            ClassLoader classLoader = new DexClassLoader(apkPath, dexDir.getAbsolutePath(), null, ctx.getClassLoader());
            Class aClass = classLoader.loadClass("com.noob.resourcesapp.R$drawable");
            int id = (int) aClass.getField("icon_collect").get(null);
            imageView.setImageDrawable(resources.getDrawable(id));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
