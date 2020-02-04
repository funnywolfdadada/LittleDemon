package com.funnywolf.littledemon.navigation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import java.lang.IllegalStateException

/**
 * @author funnywolf
 * @since 2019/12/19
 */
abstract class LifecycleLayout(context: Context): FrameLayout(context), LifecycleOwner {

    /**
     * LifecycleLayout 的生命周期
     */
    val lifecycle: LifecycleRegistry by lazy { LifecycleRegistry(this) }

    /**
     * 页面导航
     */
    var navigation: NavigationFragment? = null
        private set

    /**
     * 参数
     */
    var arguments: Bundle? = null

    protected var result: Any? = null

    init {
        visibility = View.INVISIBLE
    }

    fun initArgs(navigation: NavigationFragment) {
        this.navigation = navigation
    }

    override fun getLifecycle(): Lifecycle = lifecycle

    fun isTranslucent(): Boolean = false

    open fun onCreate() {

    }

    open fun onStart() {

    }

    open fun onResume() {

    }

    open fun onPause() {

    }

    open fun onStop() {

    }

    open fun onDestroy() {

    }

    internal fun moveTo(target: Lifecycle.State) {
        Log.d("LifecycleLayout", "$this.moveTo: ${lifecycle.currentState} -> $target")

        visibility = if (target < Lifecycle.State.STARTED) {
            View.GONE
        } else {
            View.VISIBLE
        }

        if (lifecycle.currentState < target) {
            when (lifecycle.currentState) {
                Lifecycle.State.INITIALIZED -> {
                    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
                    onCreate()
                    moveTo(target)
                }
                Lifecycle.State.CREATED -> {
                    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
                    onStart()
                    moveTo(target)
                }
                Lifecycle.State.STARTED -> {
                    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
                    onResume()
                    moveTo(target)
                }
                else -> {
                    throw IllegalStateException("$this ${lifecycle.currentState} -> $target")
                }
            }
        } else if (lifecycle.currentState > target) {
            when (lifecycle.currentState) {
                Lifecycle.State.RESUMED -> {
                    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                    onPause()
                    moveTo(target)
                }
                Lifecycle.State.STARTED -> {
                    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
                    onStop()
                    moveTo(target)
                }
                Lifecycle.State.CREATED -> {
                    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                    onDestroy()
                    moveTo(target)
                }
                else -> {
                    throw IllegalStateException("$this ${lifecycle.currentState} -> $target")
                }
            }
        }
    }

}
