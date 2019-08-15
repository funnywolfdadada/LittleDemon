package com.funnywolf.littledemon.live

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

val mainHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

fun isOnMain(): Boolean = Looper.getMainLooper().thread == Thread.currentThread()

fun runOnMain(func: ()->Unit) {
    mainHandler.post(func)
}

fun isActive(lifecycleOwner: LifecycleOwner): Boolean
        = lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)

fun isDestroy(lifecycleOwner: LifecycleOwner): Boolean
        = lifecycleOwner.lifecycle.currentState == Lifecycle.State.DESTROYED
