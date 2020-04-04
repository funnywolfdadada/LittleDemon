package com.funnywolf.littledemon.utils

import android.content.Context

private fun Context.displayMetrics() = this.resources.displayMetrics
fun Context.getScreenHeight() = displayMetrics().heightPixels
fun Float.dp2pix(context: Context) = context.displayMetrics().density * this
