package com.funnywolf.littledemon.utils

import android.util.SparseArray
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap

const val TAG = "ObserverManager"

const val STATE_LOADING = 0
const val STATE_READY = 1
const val STATE_ERROR = 2

class StateData<T> (
    val state: Int,
    val data: T? = null,
    val error: Exception? = null
)

interface Observer<T> {
    val type: Int
    fun onChanged(stateData: StateData<T>)
    fun onAdded(manager: ObserverManager) {}
    fun onRemoved(manager: ObserverManager) {}
}

/**
 * 观察者集合，管理 [Observer] 的添加、移除，及数据的分发和保存
 */
interface ObserverManager {
    /**
     * 添加观察者
     *
     * @param observer 观察者
     */
    fun addObserver(observer: Observer<*>): ObserverManager

    /**
     * 移除观察者
     *
     * @param observer 观察者
     */
    fun removeObserver(observer: Observer<*>): ObserverManager

    /**
     * 发送数据
     *
     * @param type 数据类型
     * @param state 数据状态
     * @param data 数据，可能为 null
     * @param error 出错时的 [Exception]，可能为 null
     */
    fun post(type: Int, state: Int, data: Any? = null, error: Exception? = null): ObserverManager

    /**
     * [postLoading]、[postReady] 和 [postError] 只是为了简化 [post] 操作
     */
    fun postLoading(type: Int, data: Any? = null) = post(type, STATE_LOADING, data, null)
    fun postReady(type: Int, data: Any? = null) = post(type, STATE_READY, data, null)
    fun postError(type: Int, error: Exception, data: Any? = null) = post(type, STATE_ERROR, data, error)

    /**
     * 获取数据
     *
     * @param type 数据类型
     * @param type 类型的数据，null 表示没这个数据
     */
    fun <T> getData(type: Int): StateData<T>?

}

class SimpleObserverManager: ObserverManager {

    /**
     * 以 [Observer.type] 为 key，同一 type 的 [Observer] 集合作为 value，存储当前的观察者
     */
    private val observers: SparseArray<MutableSet<Observer<*>>> = SparseArray()

    /**
     * 存储当前的数据，[ConcurrentHashMap] 保证并发安全
     */
    private val stateData = ConcurrentHashMap<Int, StateData<*>>()

    /**
     * 为了保证并发安全加的同步锁，该方法并不会频繁执行，不影响效率
     */
    @Synchronized override fun addObserver(observer: Observer<*>): ObserverManager {
        // 获得 type 对应的观察者集合，为空时创建一个
        val set = observers[observer.type]
            ?: HashSet<Observer<*>>().also {
                observers.put(observer.type, it)
            }
        // 添加新的观察者，成功添加后调用 onAdded
        if (set.add(observer)) {
            observer.onAdded(this)
        }
        return this
    }

    @Synchronized override fun removeObserver(observer: Observer<*>): ObserverManager {
        // 移除 type 类型的某个观察者，成功移除后调用 onRemoved
        if (observers[observer.type]?.remove(observer) == true) {
            observer.onRemoved(this)
        }
        return this
    }

    override fun post(type: Int, state: Int, data: Any?, error: Exception?): ObserverManager {
        // 对 type 类型的观察者集合分发数据
        (observers[type] as? MutableSet<Observer<Any>>)?.forEach {
            try {
                it.onChanged(StateData(state, data, error))
            } catch (e: Exception) {
//                Log.d(TAG, e.message)
            }
        }
        // 更新保存的数据
        stateData[type] = StateData(state, data, error)
        return this
    }

    override fun <T> getData(type: Int): StateData<T>? {
        return stateData[type] as? StateData<T>
    }

}

class LiveObserver<T>(
    override val type: Int,
    private val onChanged: (StateData<T>)->Unit,
    /**
     * 生命周期监听
     */
    private val owner: LifecycleOwner? = null,
    /**
     * 是否必须在主线程执行，默认不需要，需要的话会主动切换到主线程
     */
    private val mustRunOnMain: Boolean = false
) : Observer<T>, LifecycleEventObserver {

    private var manager: ObserverManager? = null
    private val pendingData = ArrayList<StateData<T>>()

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        // destroy 后移除观察
        if (isDestroyNonNull(owner)) {
            manager?.removeObserver(this)
            return
        }
        // 重新激活后，分发暂存的数据
        if (pendingData.isNotEmpty() && isActiveOrNull(owner)) {
            pendingData.forEach {
                dispatch(it)
            }
            pendingData.clear()
        }
    }

    override fun onChanged(stateData: StateData<T>) {
        // 需要在主线程执行，就切到主线程
        if (mustRunOnMain && !isOnMain()) {
            runOnMain { onChanged(stateData) }
            return
        }
        // 非激活状态就暂存数据
        if (isInactiveNonNull(owner)) {
            pendingData.add(stateData)
            return
        }
        // 分发数据
        dispatch(stateData)
    }

    /**
     * 分发数据，try-catch 处理异常情况
     */
    private fun dispatch(stateData: StateData<T>) {
        try {
            onChanged.invoke(stateData)
        } catch (e: Exception) {
            onChanged.invoke(stateData)
        }
    }

    override fun onAdded(manager: ObserverManager) {
        pendingData.clear()
        // 从之前的 manager 移除，引用新的
        this.manager?.removeObserver(this)
        this.manager = manager
        // 添加生命周期监听
        owner?.lifecycle?.addObserver(this)
    }

    override fun onRemoved(manager: ObserverManager) {
        pendingData.clear()
        // 移除对 manager 的引用
        this.manager = null
        // 移除生命周期观察
        owner?.lifecycle?.removeObserver(this)
    }

}

val GlobalObserverManager = SimpleObserverManager()
