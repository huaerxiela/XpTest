package com.example.xptest;

import android.util.Log;

public class HookTest {
    private static final String TAG = "HookTest";
    static {
        System.loadLibrary("hooktest");
    }

    public static boolean init(){
        return true;
    }

    public static native int add1(int x, int y);
    public static native int add2(int x, int y);

    public static void logic(){
        Log.i(TAG, "logic: add1(5,6)=" + add1(5,6));
        Log.i(TAG, "logic: add2(7,8)=" + add2(7,8));
    }
}
