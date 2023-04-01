package com.example.xptest;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cn.iinti.sekiro3.business.api.fastjson.JSONObject;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Store {
    static Object PeopleConstructorInstance;
    static Object PeopleMethodInstance;

    static Map<String, Map<String, Object>> queryResult = Maps.newConcurrentMap();
    static String keyUUID = null;

    public static String getKeyUUID() {
        return keyUUID;
    }

    public static void setKeyUUID(String keyUUID) {
        Store.keyUUID = keyUUID;
    }


    public static JSONObject callRun(String str){
        JSONObject jsonObject = new JSONObject();
        if (PeopleMethodInstance != null){
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            setKeyUUID(uuid);

            Map<String, Object> map = new HashMap<>();
            queryResult.put(uuid, map);

            XposedHelpers.callMethod(PeopleMethodInstance, "run", str);

            try {
                synchronized (uuid){
                    uuid.wait(1);
                    Map<String, Object> remove = queryResult.remove(uuid);
                    jsonObject = new JSONObject(remove);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                jsonObject.put("msg", "timeOut");
            }finally {
                setKeyUUID(null);
            }
        }else {
            jsonObject.put("msg", "PeopleMethodInstance = null");
        }
        return jsonObject;
    }


    public static boolean hookLog(ClassLoader classLoader){
        XposedHelpers.findAndHookMethod("com.hexl.lessontest.utils.LogUtils", classLoader, "info", java.lang.String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                String arg0 = (String) param.args[0];
                String uuid = getKeyUUID();
                if (uuid != null){
                    Map<String, Object> map = queryResult.get(uuid);
                    if(arg0.contains("People run by") && map != null){
                        map.put("run_param", arg0);
                        synchronized (uuid){
                            uuid.notify();
                        }
                    }
                }

            }
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
        return true;
    }
}
