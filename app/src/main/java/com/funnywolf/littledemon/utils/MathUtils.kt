package com.funnywolf.littledemon.utils

fun constrain(value: Double, min: Double, max: Double): Double {
    return when {
        value < min -> min
        value > max -> max
        else -> value
    }
}

fun constrain(value: Float, min: Float, max: Float): Float {
    return when {
        value < min -> min
        value > max -> max
        else -> value
    }
}

fun constrain(value: Int, min: Int, max: Int): Int {
    return when {
        value < min -> min
        value > max -> max
        else -> value
    }
}
