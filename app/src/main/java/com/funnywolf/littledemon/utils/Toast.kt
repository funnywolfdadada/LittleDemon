package com.funnywolf.littledemon.utils

import android.content.Context
import android.widget.Toast

/**
 * @author https://github.com/funnywolfdadada
 * @since 2020/4/4
 */
fun Context.toast(text: CharSequence?) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}