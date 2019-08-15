package com.funnywolf.littledemon.live

import androidx.lifecycle.LifecycleOwner

data class ListEvent(val type: Int, val from: Int, val len: Int, val to: Int = 0) {
    companion object {
        const val UPDATE = 1
        const val DELETE = 2
        const val ADD = 3
    }
}

class LiveList<T>: BaseLiveObservable<ListEvent>() {
    private val rawList: MutableList<T> = ArrayList()

    fun getRawList(): List<T> = rawList

}

fun test() {
    LiveList<Int>().observe({})
}
