package com.example.xptest;

import de.robv.android.xposed.XposedHelpers;

public class Store {
    static Object PeopleConstructorInstance;
    static Object PeopleMethodInstance;


    public static boolean callRun(String str){
        if (PeopleMethodInstance != null){
            return (boolean) XposedHelpers.callMethod(PeopleMethodInstance, "run", str);
        }else {
            return false;
        }
    }
}
