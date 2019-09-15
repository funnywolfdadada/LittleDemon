package com.funnywolf.littledemon.layoutmanager

import android.view.View

data class ChildChunk(
    val child: View,
    var left: Int,
    var top: Int,
    val totalWidth: Int,
    val totalHeight: Int
)
