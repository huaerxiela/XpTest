package com.example.xptest;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import java.lang.reflect.Method;

import de.hexl.android.hposed.IHposedHookLoadPackage;
import de.hexl.android.hposed.callbacks.XC_LoadPackage;


public class HookMain implements IHposedHookLoadPackage {
    private static final String TAG = "XpTest";

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Log.i(TAG, "packageName:" + lpparam.packageName);
        // 主进程xx
        if (lpparam.processName.equals(lpparam.packageName)){
            showToast(lpparam.packageName + " coming from hposed");
        }

    }

    private static void showToast(String msg){
        new Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
//                        Context context = AndroidAppHelper.currentApplication().getApplicationContext();
                        Context context = getContext();
                        if (context != null){
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                        }else {
                            Log.i(TAG, "showToast run: context = nul");
                        }
                    }
                }, 3000);
    }

    @SuppressLint("PrivateApi")
    private static Context getContext(){
        Context context = null;
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            @SuppressLint("DiscouragedPrivateApi") Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            currentActivityThreadMethod.setAccessible(true);

            Object activityThreadObj = currentActivityThreadMethod.invoke(null);

            Method currentApplicationMethod = activityThreadClass.getDeclaredMethod("currentApplication");
            currentApplicationMethod.setAccessible(true);
            Application currentApplication = (Application) currentApplicationMethod.invoke(activityThreadObj);

            assert currentApplication != null;
            context = currentApplication.getApplicationContext();
        } catch (Exception e) {
            Log.e(TAG, "getContext: ", e);
        }
        return context;
    }
}
