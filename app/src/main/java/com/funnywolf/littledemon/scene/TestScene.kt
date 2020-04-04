package com.funnywolf.littledemon.scene

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.scene.Scene
import com.funnywolf.littledemon.R
import com.funnywolf.littledemon.utils.getScreenHeight
import com.funnywolf.littledemon.utils.simpleInit
import com.funnywolf.littledemon.view.LinkedScrollView

class TestScene: Scene() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.scene_layou_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lsv = view.findViewById<LinkedScrollView>(R.id.linked_scroll)
        val rv1 = RecyclerView(view.context).apply {
            id = ViewCompat.generateViewId()
            simpleInit(50)
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, context.getScreenHeight() - 200)
        }
        lsv.setTopView(rv1, rv1)

        val rv2 = RecyclerView(view.context).apply {
            simpleInit(50, 0x80000000.toInt())
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, context.getScreenHeight() - 200)
        }
        lsv.setBottomView(rv2, rv2)
    }

}
