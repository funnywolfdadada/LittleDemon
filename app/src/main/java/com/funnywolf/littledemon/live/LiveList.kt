package com.funnywolf.littledemon.live

data class ListEvent(val type: Int, val from: Int, val len: Int, val to: Int = 0) {
    companion object {
        /**
         * 列表改变
         */
        const val ALL_CHANGED = 1
        /**
         * 列表某些项改变
         */
        const val ITEMS_CHANGED = 2
        /**
         * 列表插入某些项
         */
        const val ITEMS_INSERTED = 3
        /**
         * 列表移除某些项
         */
        const val ITEMS_REMOVED = 4
        /**
         * 列表某些项移动
         */
        const val ITEMS_MOVED = 5

        fun allChanged(from: Int, len: Int = 1) = ListEvent(ALL_CHANGED, from, len)
        fun itemsChanged(from: Int, len: Int = 1) = ListEvent(ITEMS_CHANGED, from, len)
        fun itemsInserted(from: Int, len: Int = 1) = ListEvent(ITEMS_INSERTED, from, len)
        fun itemsRemoved(from: Int, len: Int = 1) = ListEvent(ITEMS_REMOVED, from, len)
        fun itemMoved(from: Int, to: Int) = ListEvent(ITEMS_MOVED, from, 1, to)
    }
}

class LiveList<T>: BaseLiveObservable<ListEvent>(), MutableList<T> {
    private val rawList: MutableList<T> = ArrayList()

    override val size: Int = rawList.size

    override fun contains(element: T): Boolean {
        return rawList.contains(element)
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return rawList.containsAll(elements)
    }

    override fun get(index: Int): T {
        return rawList[index]
    }

    override fun indexOf(element: T): Int {
        return rawList.indexOf(element)
    }

    override fun isEmpty(): Boolean {
        return rawList.isEmpty()
    }

    override fun iterator(): MutableIterator<T> {
        return rawList.iterator()
    }

    override fun lastIndexOf(element: T): Int {
        return rawList.lastIndexOf(element)
    }

    override fun add(element: T): Boolean {
        return rawList.add(element)
    }

    override fun add(index: Int, element: T) {
        return rawList.add(index, element)
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        return rawList.addAll(index, elements)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        return rawList.addAll(elements)
    }

    override fun clear() {
        return rawList.clear()
    }

    override fun listIterator(): MutableListIterator<T> {
        return rawList.listIterator()
    }

    override fun listIterator(index: Int): MutableListIterator<T> {
        return rawList.listIterator(index)
    }

    override fun remove(element: T): Boolean {
        return rawList.remove(element)
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        return rawList.removeAll(elements)
    }

    override fun removeAt(index: Int): T {
        return rawList.removeAt(index)
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        return rawList.retainAll(elements)
    }

    override fun set(index: Int, element: T): T {
        return rawList.set(index, element)
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        return rawList.subList(fromIndex, toIndex)
    }

}
