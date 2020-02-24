package com.funnywolf.littledemon.utils

import android.os.Looper
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap

const val TAG = "StateManager"

const val STATE_LOADING = 0
const val STATE_READY = 1
const val STATE_ERROR = 2

class State (
    val type: Int,
    val state: Int,
    val data: Any? = null,
    val error: Exception? = null
)

interface StateObserver {
    fun onChanged(state: State)
    /**
     * 这里的出错指的是 [onChanged] 发生异常，而不是 State 状态为 error
     */
    fun onChangedError(e: Throwable) {}
    fun onAdded(manager: StateManager) {}
    fun onRemoved(manager: StateManager) {}
}

/**
 * 管理 [State] 的分发和保存，及 [StateObserver] 的添加、移除
 */
interface StateManager {

    /**
     * 更新 State
     *
     * @param state 新的 State
     * @return 当前的 StateManager
     */
    fun post(state: State): StateManager

    /**
     * 获取 type 类型的 State
     *
     * @param type State 类型
     * @return type 类型的 State
     */
    fun get(type: Int): State?

    /**
     * 添加观察者
     *
     * @param observer 观察者
     * @return 当前的 StateManager
     */
    fun addObserver(observer: StateObserver): StateManager

    /**
     * 移除观察者
     *
     * @param observer 观察者
     * @return 当前的 StateManager
     */
    fun removeObserver(observer: StateObserver): StateManager

}

/**
 * [postLoading]、[postReady] 和 [postError] 只是为了简化 [StateManager.post] 操作
 */
fun StateManager.postLoading(type: Int, data: Any? = null) = post(State(type, STATE_LOADING, data, null))
fun StateManager.postReady(type: Int, data: Any? = null) = post(State(type, STATE_READY, data, null))
fun StateManager.postError(type: Int, error: Exception, data: Any? = null) = post(State(type, STATE_ERROR, data, error))

class SimpleStateManager: StateManager {

    private val states = ConcurrentHashMap<Int, State?>()
    private val observers: MutableSet<StateObserver> = HashSet()

    override fun post(state: State): StateManager {
        // 分发 State
        observers.forEach {
            try {
                it.onChanged(state)
            } catch (e: Exception) {
                Log.d(TAG, e.message)
            }
        }
        // 更新 State
        states[state.type] = state
        return this
    }

    override fun get(type: Int): State? = states[type]

    /**
     * 为了保证并发安全加的同步锁，该方法并不会频繁执行，不影响效率
     */
    @Synchronized override fun addObserver(observer: StateObserver): StateManager {
        // 添加新的观察者，成功添加后调用 onAdded
        if (observers.add(observer)) {
            observer.onAdded(this)
        }
        return this
    }

    @Synchronized override fun removeObserver(observer: StateObserver): StateManager {
        // 移除某个观察者，成功移除后调用 onRemoved
        if (observers.remove(observer)) {
            observer.onRemoved(this)
        }
        return this
    }

}

/**
 * 装饰 StateObserver，提高生命周期感知和主线程切换的能力
 */
class LiveObserver(
    private val onChanged: StateObserver,
    /**
     * 生命周期监听
     */
    private val owner: LifecycleOwner? = null,
    /**
     * 是否必须在主线程执行，默认不需要，需要的话会主动切换到主线程
     */
    private val mustRunOnMain: Boolean = false
) : StateObserver, LifecycleEventObserver {

    private var manager: StateManager? = null
    private val pendingData = ArrayList<State>()

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        // destroy 后移除观察
        if (owner != null && owner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            manager?.removeObserver(this)
            return
        }
        // 重新激活后，分发暂存的数据
        if ((owner == null || owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
            && pendingData.isNotEmpty()) {
            pendingData.forEach {
                dispatch(it)
            }
            pendingData.clear()
        }
    }

    override fun onChanged(state: State) {
        // 需要在主线程执行，就切到主线程
        if (mustRunOnMain && Thread.currentThread() != Looper.getMainLooper().thread) {
            runOnMain { onChanged(state) }
            return
        }
        if (owner != null && !owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            // 非激活状态就暂存数据
            pendingData.add(state)
        } else {
            // 分发数据
            dispatch(state)
        }
    }

    /**
     * 分发数据，try-catch 处理异常情况
     */
    private fun dispatch(state: State) {
        try {
            onChanged.onChanged(state)
        } catch (e: Exception) {
            onChanged.onChangedError(e)
            Log.e(TAG, e.message)
        }
    }

    override fun onChangedError(e: Throwable) {
        // 需要在主线程执行，就切到主线程
        if (mustRunOnMain && Thread.currentThread() != Looper.getMainLooper().thread) {
            runOnMain { onChangedError(e) }
            return
        }
        onChanged.onChangedError(e)
    }

    override fun onAdded(manager: StateManager) {
        pendingData.clear()
        // 从之前的 manager 移除，引用新的
        this.manager?.removeObserver(this)
        this.manager = manager
        // 添加生命周期监听
        owner?.lifecycle?.addObserver(this)
        // 通知内部的 StateObserver
        onChanged.onAdded(manager)
    }

    override fun onRemoved(manager: StateManager) {
        pendingData.clear()
        // 移除对 manager 的引用
        this.manager = null
        // 移除生命周期观察
        owner?.lifecycle?.removeObserver(this)
        // 通知内部的 StateObserver
        onChanged.onRemoved(manager)
    }

}

val GlobalStateManager = SimpleStateManager()
