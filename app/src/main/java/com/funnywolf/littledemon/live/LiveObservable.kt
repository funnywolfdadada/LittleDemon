package com.funnywolf.littledemon.live

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

typealias Observer<T> = (T) -> Unit

interface LiveObservable<T> {
    fun observe(observer: Observer<T>, lifecycleOwner: LifecycleOwner? = null)
    fun removeObserver(observer: Observer<T>)
}

interface MutableLiveObservable<T>: LiveObservable<T> {
    fun update(data: T)
}

open class BaseLiveObservable<T>: MutableLiveObservable<T> {
    private val observersMap: MutableMap<Observer<T>, InnerObserver> = HashMap()

    override fun observe(observer: Observer<T>, lifecycleOwner: LifecycleOwner?) {
        if (lifecycleOwner?.lifecycle?.currentState == Lifecycle.State.DESTROYED) {
            return
        }
        val inner = InnerObserver(observer, lifecycleOwner)
        observersMap[observer] = inner
        lifecycleOwner?.lifecycle?.addObserver(inner)
    }

    override fun removeObserver(observer: Observer<T>) {
        observersMap.remove(observer)?.also { innerObserver ->
            innerObserver.lifecycleOwner?.also { owner ->
                owner.lifecycle.removeObserver(innerObserver)
            }
        }
    }

    override fun update(data: T) {
        if (!isOnMain()) {
            runOnMain { update(data) }
            return
        }
        observersMap.values.forEach {
            it.dispatchValue(data)
        }
    }

    private inner class InnerObserver(
        val observer: Observer<T>,
        val lifecycleOwner: LifecycleOwner? = null
    ): LifecycleEventObserver {
        private val pendingValues = ArrayList<T>()

        fun dispatchValue(data: T) {
            if (isActive()) {
                observer.invoke(data)
                return
            }
            pendingValues.add(data)
        }

        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (isDestroy()) {
                removeObserver(observer)
            } else if (isActive() && pendingValues.size > 0) {
                pendingValues.forEach { dispatchValue(it) }
                pendingValues.clear()
            }
        }

        private fun isActive(): Boolean = (lifecycleOwner == null) || (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))

        private fun isDestroy(): Boolean = lifecycleOwner?.lifecycle?.currentState == Lifecycle.State.DESTROYED
    }

}
