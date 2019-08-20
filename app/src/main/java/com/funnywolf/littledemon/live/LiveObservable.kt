package com.funnywolf.littledemon.live

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.lang.Exception

typealias Observer<T> = (T?) -> Unit

interface LiveObservable<T> {
    /**
     * 添加观察者
     *
     * @param observer 观察者
     * @param lifecycleOwner 要感知的生命周期
     * @param dispatchAtInactive 在非激活状态下是否分发数据，默认不分发
     */
    fun addObserver(observer: Observer<T>, lifecycleOwner: LifecycleOwner? = null,
                dispatchAtInactive: Boolean = false)
    /**
     * 移除观察者
     *
     * @param observer 观察者
     */
    fun removeObserver(observer: Observer<T>)
}

interface MutableLiveObservable<T>: LiveObservable<T> {
    /**
     * 分发数据
     *
     * @param value 新的数据，可空
     */
    fun dispatchValue(value: T?)
}

open class BaseLiveObservable<T>: MutableLiveObservable<T> {
    private val observersMap: MutableMap<Observer<T>, InnerObserver> = HashMap()

    /**
     * 是否在主线程更新
     */
    open fun updateOnMain(): Boolean = true

    /**
     * 是否处理异常
     */
    open fun handleError(e: Exception): Boolean = true

    override fun addObserver(observer: Observer<T>, lifecycleOwner: LifecycleOwner?, dispatchAtInactive: Boolean) {
        assertOnMain("BaseLiveObservable.observe")
        // 如果已经添加，或者已经生命周期结束，就直接退出
        if (observersMap.containsKey(observer) || isDestroyNonNull(lifecycleOwner)) {
            return
        }
        observersMap[observer] = InnerObserver(observer, lifecycleOwner, dispatchAtInactive)
    }

    override fun removeObserver(observer: Observer<T>) {
        assertOnMain("BaseLiveObservable.removeObserver")
        observersMap.remove(observer)?.also { innerObserver ->
            innerObserver.clear()
        }
    }

    override fun dispatchValue(value: T?) {
        // 如果需要在主线程执行，又不在主线程，就抛到主线程执行
        if (updateOnMain() && !isOnMain()) {
            runOnMain { dispatchValue(value) }
            return
        }
        // 分发
        observersMap.values.forEach {
            try {
                it.dispatchValue(value)
            } catch (e: Exception) {
                // 如果不处理 error 就再次抛出去
                if (!handleError(e)) {
                    throw e
                }
            }
        }
    }

    private inner class InnerObserver(
        val observer: Observer<T>,
        var lifecycleOwner: LifecycleOwner? = null,
        val dispatchAtInactive: Boolean
    ): LifecycleEventObserver {
        /**
         * 保存非激活状态保存的值，并在激活后分发出去
         */
        private val pendingValues = ArrayList<T?>()

        init {
            // 监听生命周期
            lifecycleOwner?.lifecycle?.addObserver(this)
        }

        /**
         * 更新 value
         */
        fun dispatchValue(data: T?) {
            if (isInactiveNonNull(lifecycleOwner) && !dispatchAtInactive) {
                // 如果未激活，且不允许在非激活时更新，则把数据暂存起来
                pendingValues.add(data)
            } else {
                // 其他情况直接更新
                observer.invoke(data)
            }
        }

        /**
         * 解除生命周期监听，并清空 [pendingValues]
         */
        fun clear() {
            lifecycleOwner?.lifecycle?.removeObserver(this)
            lifecycleOwner = null
            pendingValues.clear()
        }

        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (isDestroyNonNull(lifecycleOwner)) {
                // 生命周期结束就移除观察者
                removeObserver(observer)
            } else if (isActiveOrNull(lifecycleOwner) && pendingValues.size > 0) {
                // 激活后如果有暂存的 value 就全部分发出去
                pendingValues.forEach { dispatchValue(it) }
                pendingValues.clear()
            }
        }

    }

}
