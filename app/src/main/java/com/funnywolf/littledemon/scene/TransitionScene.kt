package com.funnywolf.littledemon.scene

import android.animation.*
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.transition.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.scene.Scene
import com.funnywolf.littledemon.R

/**
 * @author https://github.com/funnywolfdadada
 * @since 2020/10/11
 */
class TransitionScene: Scene() {
    private lateinit var rootView: ViewGroup
    private var startScene: android.transition.Scene? = null
    private var endScene: android.transition.Scene? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        rootView = FrameLayout(container.context)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        openStartScene()
    }

    private fun openStartScene() {
        if (startScene == null) {
            val startView = LayoutInflater.from(rootView.context).inflate(R.layout.scene_layout_transition_start, rootView, false).apply {
                findViewById<View>(R.id.open_layout_0)?.setOnClickListener { openEndLayout(it) }
                findViewById<View>(R.id.open_layout_1)?.setOnClickListener { openEndLayout(it) }
                findViewById<View>(R.id.open_layout_2)?.setOnClickListener { openEndLayout(it) }

                findViewById<View>(R.id.open_activity_0)?.setOnClickListener { openEndActivity(R.id.open_activity_0) }
                findViewById<View>(R.id.open_activity_1)?.setOnClickListener { openEndActivity(R.id.open_activity_1) }
                findViewById<View>(R.id.open_activity_2)?.setOnClickListener { openEndActivity(R.id.open_activity_2) }
            }
            startScene = Scene(rootView, startView)
        }
        TransitionManager.go(startScene)
    }

    private fun openEndLayout(v: View) {
        if (endScene == null) {
            val endView = LayoutInflater.from(rootView.context).inflate(R.layout.scene_layout_transition_end, rootView, false).apply {
                findViewById<View>(R.id.dest_view)?.setOnClickListener { openStartScene() }
            }
            endScene = Scene(rootView, endView)
        }

        TransitionManager.go(endScene, ClipBounds(v.getScreenBounds(), R.id.dest_container))
    }

    private fun openEndActivity(id: Int) {
        val act = activity ?: return
        act.window.apply {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        }
        val v = findViewById<View>(id)!!
        val opts = ActivityOptions.makeSceneTransitionAnimation(act)
//        val opts = ActivityOptions.makeSceneTransitionAnimation(act, v, START_VIEW_BOUNDS)
        act.startActivity(Intent(act, TransitionActivity::class.java).apply {
            putExtra(START_VIEW_BOUNDS, v.getScreenBounds())
        }, opts.toBundle())
    }

}

fun View.getScreenBounds() = Rect().apply {
    val xy = IntArray(2)
    getLocationOnScreen(xy)
    set(xy[0], xy[1], xy[0] + width, xy[1] + height)
}

const val START_VIEW_BOUNDS = "START_VIEW_BOUNDS"

class ClipBounds(val bound: Rect, val id: Int): Visibility() {

//    override fun captureStartValues(transitionValues: TransitionValues?) {
//        super.captureStartValues(transitionValues)
//        Log.d("zdl", "start: ${transitionValues?.view.toString()}")
//    }
//
//    override fun captureEndValues(transitionValues: TransitionValues?) {
//        super.captureEndValues(transitionValues)
//        Log.d("zdl", "end: ${transitionValues?.view.toString()}")
//    }
//
//    override fun createAnimator(
//        sceneRoot: ViewGroup?,
//        startValues: TransitionValues?,
//        endValues: TransitionValues?
//    ): Animator? {
//        Log.d("zdl", "createAnimator: start: ${startValues?.view}, end: ${endValues?.view}")
//        return super.createAnimator(sceneRoot, startValues, endValues)
//    }

    override fun onAppear(
        sceneRoot: ViewGroup?,
        view: View?,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        Log.d("zdl", "onAppear: $view")
        return if (view?.id == id) {
            animator(view, true)
        } else {
            super.onDisappear(sceneRoot, view, startValues, endValues)
        }
    }

    override fun onDisappear(
        sceneRoot: ViewGroup?,
        view: View?,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        Log.d("zdl", "onDisappear: $view")
        return if (view?.id == id) {
            animator(view, false)
        } else {
            super.onDisappear(sceneRoot, view, startValues, endValues)
        }
    }

    private fun animator(view: View, visible: Boolean): Animator? {
        val origin = view.clipBounds
        val start = Rect(bound)
        IntArray(2).also {
            view.getLocationOnScreen(it)
            start.offset(-it[0], -it[1])
        }
        val end = Rect(0, 0, view.width, view.height)
        if (start == end) {
            return null
        }
        return if (visible) {
            ObjectAnimator.ofObject(view, "clipBounds", RectEvaluator(), start, end)
        } else {
            ObjectAnimator.ofObject(view, "clipBounds", RectEvaluator(), end, start)
        }.apply {
            addListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    view.clipBounds = origin
                }
            })
        }
    }

}

class CustomBounds: ChangeBounds() {

    override fun captureStartValues(transitionValues: TransitionValues?) {
        super.captureStartValues(transitionValues)
        Log.d("zdl", "start: ${transitionValues?.view.toString()}")
    }

    override fun captureEndValues(transitionValues: TransitionValues?) {
        super.captureEndValues(transitionValues)
        Log.d("zdl", "end: ${transitionValues?.view.toString()}")
    }

    override fun createAnimator(
        sceneRoot: ViewGroup?,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        return super.createAnimator(sceneRoot, startValues, endValues)?.apply {
            Log.d("zdl", "createAnimator: start: ${startValues?.view?.getScreenBounds()}, end: ${endValues?.view?.getScreenBounds()}")
        }
    }

}

class TransitionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.apply {
            setBackgroundDrawable(ColorDrawable(0))
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)

            val bound = intent.getParcelableExtra<Rect>(START_VIEW_BOUNDS)
            enterTransition = ClipBounds(bound, R.id.dest_container)
            exitTransition = ClipBounds(bound, R.id.dest_container)

//            enterTransition = Fade()
//            exitTransition = Fade()

//            sharedElementEnterTransition = CustomBounds()
//            sharedElementExitTransition = CustomBounds()

        }
        setContentView(R.layout.scene_layout_transition_end)

        findViewById<View>(R.id.dest_container).transitionName = START_VIEW_BOUNDS

        findViewById<View>(R.id.dest_view)?.apply {
            setOnClickListener {
                finishAfterTransition()
            }
        }

    }

}