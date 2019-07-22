package com.funnywolf.littledemon.utils;

import android.os.Handler;
import android.os.Looper;

public class MainHandlerUtils {
    public static void post(Runnable runnable) {
        MainHandler.handler.post(runnable);
    }

    private static class MainHandler {
        private static Handler handler = new Handler(Looper.getMainLooper());
    }
}
