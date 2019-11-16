package com.funnywolf.littledemon.utils

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

val mainHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

fun isOnMain(): Boolean = Looper.getMainLooper().thread == Thread.currentThread()

fun assertOnMain(methodName: String) {
    check(isOnMain()) { "Cannot invoke $methodName on a background thread" }
}

fun runOnMain(func: ()->Unit) {
    mainHandler.post(func)
}

fun isActive(lifecycleOwner: LifecycleOwner): Boolean
        = lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)

fun isDestroy(lifecycleOwner: LifecycleOwner): Boolean
        = lifecycleOwner.lifecycle.currentState == Lifecycle.State.DESTROYED

fun isActiveOrNull(lifecycleOwner: LifecycleOwner?): Boolean
        = lifecycleOwner == null || isActive(lifecycleOwner)

fun isInactiveNonNull(lifecycleOwner: LifecycleOwner?): Boolean
        = !isActiveOrNull(lifecycleOwner)

fun isDestroyNonNull(lifecycleOwner: LifecycleOwner?): Boolean
        = lifecycleOwner != null && isDestroy(lifecycleOwner)
