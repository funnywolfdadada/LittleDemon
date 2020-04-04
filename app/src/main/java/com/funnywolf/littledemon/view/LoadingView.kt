package com.funnywolf.littledemon.view

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import com.funnywolf.littledemon.R

class LoadingView: ImageView {

    private var loadingAnimator: ValueAnimator? = null

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)

    init {
        setImageResource(R.drawable.ic_loading)
    }

    fun loading(loading: Boolean) {
        if (loading) {
            start()
        } else {
            stop()
        }
    }

    private fun start() {
        stop()
        ValueAnimator.ofFloat(0F, 1F).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            addUpdateListener {
                setProgress(it.animatedValue as? Float ?: return@addUpdateListener)
            }
            loadingAnimator = this
            start()
        }
    }

    private fun stop() {
        setProgress(0F)
        loadingAnimator?.cancel()
    }

    fun setProgress(progress: Float) {
        rotation = -360 * 3 * progress
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }
}