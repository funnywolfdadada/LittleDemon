package com.funnywolf.littledemon.test

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 测试协程
 *
 * @author zhaodongliang @ Zhihu Inc.
 * @since 06-18-2019
 */
const val TAG = "TestCoroutine"

fun testCoroutine() {
    GlobalScope.launch(Dispatchers.Main) {
        val pA = 1
        val a = withContext(Dispatchers.IO) { getDataA(pA) }
        val pB = doSomethingWithA(a)
        val b = withContext(Dispatchers.IO) { getDataB(pB) }
        val result = doSomethingWithB(b)
        Log.d(TAG, "result: $result")
    }
}

fun getDataA(pA: Int): String {
    Log.d(TAG, "getDataA: ${Thread.currentThread().name}")
    Thread.sleep(1000)
    return (pA + 1).toString()
}

fun doSomethingWithA(a: String): Int {
    Log.d(TAG, "doSomethingWithA: ${Thread.currentThread().name}")
    return (a + 1).toInt()
}

fun getDataB(pB: Int): String {
    Log.d(TAG, "getDataB: ${Thread.currentThread().name}")
    Thread.sleep(1000)
    return (pB + 1).toString()
}

fun doSomethingWithB(b: String): Int {
    Log.d(TAG, "doSomethingWithB: ${Thread.currentThread().name}")
    return (b + 1).toInt()
}


