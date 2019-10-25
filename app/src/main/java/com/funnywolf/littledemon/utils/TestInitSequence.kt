package com.funnywolf.littledemon.utils

open class Father {

    private val a = "a".also {
        println("Father: $it")
    }

    init {
        println("Father: init block 1")
    }

    private val b = "b - $companionB".also {
        println("Father: $it")
    }

    init {
        println("Father: init block 2")
    }

    private val sth: String

    constructor(data: String): super() {
        sth = data
        println("Father: constructor $data")
    }

    open fun doSth() {
        println("Father: doSth $sth $a $b")
    }

    companion object {

        private val companionB = "companionB".also {
            println("Father: $it")
        }

        init {
            println("Father: companion init block")
        }

    }
}

class Child: Father {

    private val aa = "aa".also {
        println("Child: $it")
    }

    init {
        println("Child: init block 1")
    }

    private val bb = "bb - $companionB".also {
        println("Child: $it")
    }

    init {
        println("Child: init block 2")
    }

    constructor(data: String): super(data) {
        println("Child: constructor $data")
    }

    override fun doSth() {
        super.doSth()
        println("Child: doSth $aa $bb")
    }

    companion object {

        private val companionB = "companionB".also {
            println("Child: $it")
        }

        init {
            println("Child: companion init block")
        }

    }
}

val out  = "out".also {
    println("test: $it")
}

fun test() {
    println("before test")
    Child(out).doSth()
    println("after test")
}
