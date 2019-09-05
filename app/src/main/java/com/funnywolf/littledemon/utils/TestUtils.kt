package com.funnywolf.littledemon.utils

import android.widget.TextView
import android.widget.Toast
import com.funnywolf.littledemon.R
import com.funnywolf.littledemon.simpleadapter.SimpleAdapter
import kotlin.random.Random

fun createSimpleStringHolderInfo(): SimpleAdapter.HolderInfo<String> {
    return SimpleAdapter.HolderInfo(String::class.java, R.layout.view_layout_simple_view_holder, { holder ->
        holder.itemView.setOnClickListener {
            Toast.makeText(it.context, "Clicked ${holder.currentData}", Toast.LENGTH_SHORT).show()
        }
    }, { holder ->
        holder.getView<TextView>(R.id.content).text = holder.currentData
    })
}

fun getRandomStrings(n: Int, prefixIndex: Boolean = true, prefix: String = "", suffix: String = "", size: Int = 0): MutableList<String> {
    return MutableList(n) {
        val count = if (size <= 0) {
            (Math.random() * 3 + 7).toInt()
        } else {
            size
        }
        val array = CharArray(count) {
            'A' + (Math.random() * 26).toInt()
        }
        val index = if (prefixIndex) {
            "$it:"
        } else {
            ""
        }
        "$index $prefix ${String(array)} $suffix"
    }
}

fun getRandomInt(n: Int, start: Int = 0, end: Int = Int.MAX_VALUE): MutableList<Int> {
    return MutableList(n) {
        Random.nextInt(start, end)
    }
}
