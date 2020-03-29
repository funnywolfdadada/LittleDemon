package com.funnywolf.littledemon.utils

import android.widget.TextView
import android.widget.Toast
import com.funnywolf.hollowkit.recyclerview.HolderInfo
import com.funnywolf.littledemon.R
import kotlin.math.round
import kotlin.random.Random

fun createSimpleStringHolderInfo(): HolderInfo<String> {
    return HolderInfo(String::class.java, R.layout.view_layout_simple_view_holder,
        onCreate = { holder ->
            holder.itemView.setOnClickListener {
                Toast.makeText(it.context, "Clicked ${holder.data}", Toast.LENGTH_SHORT).show()
            }
        },
        onBind = { holder, data ->
            holder.v<TextView>(R.id.content)?.text = data
        })
}

fun getRandomString(length: Int = (Math.random() * 3 + 7).toInt()): String {
    return String(CharArray(length) {
        'A' + (Math.random() * 26).toInt()
    })
}

fun getRandomStrings(n: Int, prefixIndex: Boolean = true, prefix: String = "", suffix: String = "",
                     sizeMin: Int = 3, sizeMax: Int = 10): MutableList<String> {
    return MutableList(n) {
        "${
            if (prefixIndex) { "$it:" } else { "" }
        }$prefix${
            getRandomString(round(Math.random() * (sizeMax - sizeMin) + sizeMin).toInt())
        }$suffix"
    }
}

fun getRandomInt(n: Int, start: Int = 0, end: Int = Int.MAX_VALUE): MutableList<Int> {
    return MutableList(n) {
        Random.nextInt(start, end)
    }
}
