package com.example.xptest;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.callbacks.XCallback;

public class HookMain implements IXposedHookLoadPackage, IXposedHookInitPackageResources, IXposedHookZygoteInit {
    private static final String TAG = "XpTest";

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Log.i(TAG, "packageName:" + lpparam.packageName);
        String nativeLibraryDir = lpparam.appInfo.nativeLibraryDir;
        ClassLoader classLoader = lpparam.classLoader;
        boolean isFirstApplication = lpparam.isFirstApplication;
        Log.i(TAG, "handleLoadPackage: nativeLibraryDir = " + nativeLibraryDir);
        Log.i(TAG, "handleLoadPackage: classLoader = " + classLoader);
        Log.i(TAG, "handleLoadPackage: isFirstApplication = " + isFirstApplication);
        // 主进程xx
        if (lpparam.processName.equals(lpparam.packageName)){
            showToast(lpparam.packageName + " coming");
            Class<?> People = XposedHelpers.findClassIfExists("com.hexl.lessontest.logic.People", lpparam.classLoader);
            Field hello = XposedHelpers.findField(People, "hello");
            hello.setAccessible(true);
            String world = (String) hello.get(null);
            Log.i(TAG, "handleLoadPackage: hello = " + world);
            Method run = XposedHelpers.findMethodExact(People, "run", String.class);
            boolean runResult = (boolean) run.invoke(XposedHelpers.newInstance(People, 1), "111111");
            Log.i(TAG, "handleLoadPackage: runResult = " + runResult);
            XposedHelpers.findAndHookMethod(People, "run", String.class, new XC_MethodHook(XCallback.PRIORITY_HIGHEST) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    String arg0 = (String) param.args[0];
                    Object obj = param.thisObject;
                    Log.i(TAG, "beforeHookedMethod: arg0 = " + arg0 + " ,, obj = " + obj);
                    param.args[0] = "gebilaohua";
                    Log.i(TAG, Log.getStackTraceString(new Throwable()));
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    boolean result = (boolean) param.getResult();
                    Log.i(TAG, "afterHookedMethod: result = " + result);
                    param.setResult(false);
                    Log.i(TAG, "afterHookedMethod: fakeResult = " + param.getResult());
                }
            });

            XposedHelpers.findAndHookConstructor("com.hexl.lessontest.logic.People", lpparam.classLoader, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Log.i(TAG, "beforeHookedMethod: findAndHookConstructor before");
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Object obj = param.thisObject;
                    Log.i(TAG, "afterHookedMethod: findAndHookConstructor after: " + obj);
                }
            });

//            XposedBridge.hookAllMethods(People, "speak", new XC_MethodHook() {
//                @Override
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                    super.beforeHookedMethod(param);
//                    Log.i(TAG, "beforeHookedMethod: hookAllMethods：" + Arrays.toString(param.args));
//                }
//
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    super.afterHookedMethod(param);
//                    Log.i(TAG, "afterHookedMethod: hookAllMethods：" + param.getResult());
//                }
//            });
//            XposedBridge.hookAllConstructors();
//            XposedBridge.invokeOriginalMethod()
//            XposedBridge.log();
//            XposedBridge.getXposedVersion();
//            XposedBridge.hookMethod()

            XposedBridge.hookAllMethods(People, "speak", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    Log.i(TAG, "replaceHookedMethod: " + Arrays.toString(param.args));
                    if (param.args.length == 1){
                        return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                    }
                    return null;
                }
            });

            Object peopleObj = XposedHelpers.newInstance(People, "gebilaohua");
            XposedHelpers.setObjectField(peopleObj, "name", "gebixiaohua");
            XposedHelpers.setStaticBooleanField(People, "speak", false);

            String name = (String) XposedHelpers.getObjectField(peopleObj, "name");
            String name2 = (String) XposedHelpers.getObjectField(XposedHelpers.newInstance(People, "gebilaohua"), "name");
            boolean speak = XposedHelpers.getStaticBooleanField(People, "speak");
            Log.i(TAG, "handleLoadPackage: name = " + name);
            Log.i(TAG, "handleLoadPackage: name2 = " + name2);
            Log.i(TAG, "handleLoadPackage: speak = " + speak);

            XposedHelpers.setAdditionalInstanceField(peopleObj, "chatgpt", "gebilaohua");
            XposedHelpers.setAdditionalStaticField(People, "chat", "gpt");
            String chatgpt = (String) XposedHelpers.getAdditionalInstanceField(peopleObj, "chatgpt");
            String gpt = (String) XposedHelpers.getAdditionalStaticField(People, "chat");
            Log.i(TAG, "handleLoadPackage: chatgpt = " + chatgpt);
            Log.i(TAG, "handleLoadPackage: gpt = " + gpt);


        }



    }

    private static void showToast(String msg){
        new Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        Context context = AndroidAppHelper.currentApplication().getApplicationContext();
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }
                }, 3000);
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        String packageName1 = resparam.res.getPackageName();
        String packageName2 = resparam.packageName;
        Log.i(TAG, "handleInitPackageResources: " + packageName1 + "  ,  "  +  packageName2);
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        String modulePath = startupParam.modulePath;
        boolean startsSystemServer = startupParam.startsSystemServer;
        Log.i(TAG, "initZygote: " + modulePath + "  ,  " + startsSystemServer);
    }
}
