package com.example.xptest;

import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import com.example.xptest.handler.TestHandler;
import com.example.xptest.handler.TwoHandler;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import cn.iinti.sekiro3.business.api.SekiroClient;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
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
        if (lpparam.processName.equals(lpparam.packageName) && !BuildConfig.APPLICATION_ID.equals(lpparam.packageName)){
            showToast(lpparam.packageName + " coming");

//            connectServer();
//            test(lpparam.classLoader);

//            testPre();

//            Class<?> PeopleClass = XposedHelpers.findClassIfExists("com.hexl.lessontest.logic.People", lpparam.classLoader);
//            if (PeopleClass != null){
//                XposedBridge.hookAllConstructors(PeopleClass, new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        super.afterHookedMethod(param);
//                        Store.PeopleMethodInstance = param.thisObject;
//                    }
//                });
//            }
//
//            Store.hookLog(lpparam.classLoader);


            hookJiaGu(lpparam);


        }



    }

    private static void hookJiaGu(final XC_LoadPackage.LoadPackageParam lpparam){
        hookJiaGuApp(lpparam.packageName, lpparam.classLoader, "org");

        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                // 获取应用程序的 Context 对象
                Context context = (Context) param.args[0];
                hookJiaGuApp(lpparam.packageName, context.getClassLoader(), "attach");
            }
        });

        final Class<?> Instrumentation = XposedHelpers.findClass("android.app.Instrumentation", null);
        XposedHelpers.findAndHookMethod(
                Instrumentation,
                "callApplicationOnCreate",
                Application.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Context context = ((Application) param.args[0]).getApplicationContext();
                        hookJiaGuApp(lpparam.packageName, context.getClassLoader(), "callApplicationOnCreate");
                    }
                }
        );

        Class<?> ActivityThread = XposedHelpers.findClass("android.app.ActivityThread", null);
        XposedBridge.hookAllMethods(ActivityThread, "performLaunchActivity", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Object mInitialApplication = XposedHelpers.getObjectField(param.thisObject,"mInitialApplication");
                ClassLoader finalCL = (ClassLoader) XposedHelpers.callMethod(mInitialApplication,"getClassLoader");

                hookJiaGuApp(lpparam.packageName, finalCL, "performLaunchActivity");

            }
        });
    }

    private static void hookJiaGuApp(String packageName, ClassLoader classLoader, String sourceTag){
        if ("com.pupumall.customer".equals(packageName)){
            Class<?> SplashActivityClass = XposedHelpers.findClassIfExists("com.pupumall.customer.activity.SplashActivity", classLoader);
            Log.i(TAG, "handleLoadPackage: SplashActivityClass = " + SplashActivityClass + "  , sourceTag = " + sourceTag);

        }
        if ("com.avalon.caveonline.cn.leiting".equals(packageName)){
            Class<?> PrivacyActivityClass = XposedHelpers.findClassIfExists("com.leiting.sdk.activity.PrivacyActivity", classLoader);
            Log.i(TAG, "handleLoadPackage: PrivacyActivityClass = " + PrivacyActivityClass + "  , sourceTag = " + sourceTag);

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

    private static XSharedPreferences getPref(String path) {
        XSharedPreferences pref = new XSharedPreferences(BuildConfig.APPLICATION_ID, path);
        return pref.getFile().canRead() ? pref : null;
    }

    private boolean connectServer(){
        String web = testPre();
        assert web != null;
        String[] ip_port = web.split(":");
        SekiroClient sekiroClient = new SekiroClient("test", "hexl123", ip_port[0], Integer.parseInt(ip_port[1]));
        new Thread(new Runnable() {
            @Override
            public void run() {
                sekiroClient.setupSekiroRequestInitializer((sekiroRequest, handlerRegistry) -> {
                    // 注册一个接口，名为testAction
                    handlerRegistry.registerSekiroHandler(new TestHandler());
                    handlerRegistry.registerSekiroHandler(new TwoHandler());
                }).start();
            }
        }).start();
        return true;
    }

    private String testPre(){
        XSharedPreferences sharedPreferences = getPref("TestSetting");
        if (sharedPreferences == null){
            Log.i(TAG, "handleLoadPackage: sharedPreferences = null");
            return null;
        }else {
            sharedPreferences.reload();
            String web = sharedPreferences.getString("web", "");
            Log.i(TAG, "handleLoadPackage: web = " + web);
            return web;
        }
    }

    private void test(ClassLoader classLoader){
        Class<?> People = XposedHelpers.findClassIfExists("com.hexl.lessontest.logic.People", classLoader);
            /*
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
            */

        XposedHelpers.findAndHookConstructor("com.hexl.lessontest.logic.People", classLoader, new XC_MethodHook() {
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
                Store.PeopleConstructorInstance = obj;
            }
        });


        XposedHelpers.callStaticMethod(People, "speak", "20230311", 21);
        Object PeopleInstance = XposedHelpers.newInstance(People, 10, "101010");
//            Store.PeopleConstructorInstance;
        XposedHelpers.callMethod(Store.PeopleConstructorInstance, "run", "111111");
        Log.i(TAG, "handleLoadPackage: call = success");
            /*
            public static void speak(xxx ooo, int i) {
                xxxxddfdaf
                String sign = ooo.getSign("url=xxxxxx?keyword=erji");
                map.put("sign“， sign)；
            }
             */
    }
}
