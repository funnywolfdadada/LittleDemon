package com.funnywolf.littledemon.layoutmanager

import androidx.annotation.IntDef

@IntDef(value = [
    AlignContent.START,
    AlignContent.END,
    AlignContent.CENTER,
    AlignContent.CENTER_SPREAD,
    AlignContent.CENTER_PACKED
])
@Retention(AnnotationRetention.SOURCE)
annotation class AlignContent {
    companion object {
        /**
         * 内容左对齐
         */
        const val START = 0
        /**
         * 内容右对齐
         */
        const val END = 1
        /**
         * 内容居中，均分
         */
        const val CENTER = 2
        /**
         * 内容居中，分散
         */
        const val CENTER_SPREAD = 3
        /**
         * 内容居中，集中
         */
        const val CENTER_PACKED = 4
    }
}