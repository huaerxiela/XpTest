package com.example.xptest;

public class HookTest {
    static {
        System.loadLibrary("hooktest");
    }

    public static boolean init(){
        return true;
    }
}
