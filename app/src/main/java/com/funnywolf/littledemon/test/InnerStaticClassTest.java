package com.funnywolf.littledemon.test;

import android.util.Log;

public class InnerStaticClassTest {
    public static final String TAG = "InnerStaticClassTest";

    static {
        Log.d(TAG, "InnerStaticClassTest: static init: " + System.currentTimeMillis());
    }

    public static void doSomethingSelf() {
        Log.d(TAG, "InnerStaticClassTest: doSomethingSelf: " + System.currentTimeMillis());
    }

    public static void doSomethingWithInnerStaticClass() {
        Log.d(TAG, "doSomethingWithInnerStaticClass: "
                + StaticInnerClass.staticField + ": " + System.currentTimeMillis());
    }

    private static class StaticInnerClass {
        static {
            Log.d(TAG, "StaticInnerClass: static init: " + System.currentTimeMillis());
        }
        private static String staticField = "staticField";
    }
}
