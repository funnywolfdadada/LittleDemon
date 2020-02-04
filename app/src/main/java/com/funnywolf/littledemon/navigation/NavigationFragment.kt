package com.funnywolf.littledemon.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import retrofit2.http.OPTIONS
import java.lang.ref.WeakReference
import kotlin.math.min

/**
 * @author funnywolf
 * @since 2019/12/19
 */
class NavigationFragment : Fragment(), LifecycleObserver {
    private val container: ViewGroup by lazy { FrameLayout(context!!) }

    private val stack = ArrayList<PushOption>()

    init {
        lifecycle.addObserver(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return this.container.apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        Log.d("NavigationFragment", "onStateChanged: $event ${lifecycle.currentState} ${source == this@NavigationFragment}")
        // 指向栈顶
        var i = stack.size - 1
        if (i >= 0) {
            // 同步栈顶的生命周期
            stack[i].layout.moveTo(lifecycle.currentState)
        } else {
            // 栈空时就直接返回
            return
        }

        // 栈顶页面时半透明的，需要处理部分可见页面的生命周期
        if (stack[i].layout.isTranslucent()) {
            val forPartiallyShowing = if(lifecycle.currentState <= Lifecycle.State.STARTED) {
                lifecycle.currentState
            } else {
                Lifecycle.State.STARTED
            }
            // 先指向栈顶下一个
            while (--i >= 0) {
                val layout = stack[i].layout
                layout.moveTo(forPartiallyShowing)
                if (!layout.isTranslucent()) {
                    break
                }
            }
        }

        // 同步不可见页面的生命周期
        val forBackground = if(lifecycle.currentState <= Lifecycle.State.CREATED) {
            lifecycle.currentState
        } else {
            Lifecycle.State.CREATED
        }
        // 当前 i 指向从栈顶开始的第一个不透明页面，需要先指向下一个
        while (--i >= 0) {
            stack[i].layout.moveTo(forBackground)
        }
    }

    fun push(opt: PushOption) {
        stack.add(opt)
        container.addView(opt.layout)
        opt.layout.initArgs(this)
        opt.layout.moveTo(lifecycle.currentState)
    }

    fun pop(opt: PopOption) {

    }

    private fun pop(opt: PushOption) {
        if (!stack.contains(opt)) {
            return
        }
        opt.layout.moveTo(Lifecycle.State.DESTROYED)
        opt.onResultRef?.get()?.invoke(opt.layout.arguments)
        container.removeView(opt.layout)
        stack.remove(opt)
    }

}

class PushOption(val layout: LifecycleLayout) {

    val layoutClass: Class<out LifecycleLayout> = layout.javaClass
    var onResultRef: WeakReference<((Any?) -> Unit)>? = null

    fun setArguments(arguments: Bundle): PushOption {
        layout.arguments = arguments
        return this
    }

    fun onResult(callback: ((Any?)->Unit)): PushOption {
        onResultRef = WeakReference(callback)
        return this
    }

}

class PopOption (

)
