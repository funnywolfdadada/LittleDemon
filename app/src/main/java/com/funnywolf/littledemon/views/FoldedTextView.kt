package com.funnywolf.littledemon.views

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.funnywolf.littledemon.utils.dp2pix

class FoldedTextView: TextView {

    private var foldedLines = maxLines
    private var isFolded = maxLines != Int.MAX_VALUE
    private var isFolding = false

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)

    fun setFoldedLines(lines: Int) {
        if (lines == foldedLines) { return }
        foldedLines = lines
        fold(isFolded)
    }

    fun isFolded() = isFolded

    fun fold(fold: Boolean) {
        if (isFolding || fold == isFolded) { return }
        isFolding = true
        isFolded = fold
        val startHeight = height
        val targetLines = when {
            isFolded -> foldedLines
            ellipsize == null -> lineCount
            else -> (paint.measureText(text.toString()) / width + 0.999).toInt()
        }
        val targetHeight = targetLines * lineHeight + paddingBottom + paddingTop
        val animator = ValueAnimator.ofInt(startHeight, targetHeight)
        animator.addUpdateListener {
            (it.animatedValue as? Int)?.apply {
                if (this == startHeight) {
                    if (!isFolded) { maxLines = Int.MAX_VALUE }
                } else if (this == targetHeight) {
                    if (isFolded) { maxLines = foldedLines }
                    isFolding = false
                }
                layoutParams.height = this
                if (this == targetHeight) {
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
                layoutParams = layoutParams
            }
        }
        animator.start()
    }

    fun setText(text: CharSequence?, fold: Boolean) {
        setText(text)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        widthMeasureSpec
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        widthMeasureSpec
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (ellipsize == null) {
            if (lineCount > maxLines) {
                Toast.makeText(context, "行数超出 $maxLines", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (isEllipsized()) {
                Toast.makeText(context, "行数超出 $maxLines", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isEllipsized(): Boolean {
        repeat(lineCount) {
            if (layout.getEllipsisCount(it) > 0) {
                return true
            }
        }
        return false
    }

}
