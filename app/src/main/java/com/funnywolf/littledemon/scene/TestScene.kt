package com.funnywolf.littledemon.scene

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.scene.Scene
import com.funnywolf.hollowkit.recyclerview.HolderInfo
import com.funnywolf.hollowkit.recyclerview.SimpleAdapter
import com.funnywolf.hollowkit.recyclerview.SimpleHolder
import com.funnywolf.littledemon.R
import com.funnywolf.littledemon.utils.createSimpleStringHolderInfo
import com.funnywolf.littledemon.utils.getRandomStrings
import com.funnywolf.littledemon.utils.simpleInit
import com.funnywolf.littledemon.utils.toast
import com.funnywolf.littledemon.view.JellyLayout

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

        findViewById<RecyclerView>(R.id.recycler_view)?.also {
            val list = ArrayList<Any>()
            list.addAll(getRandomStrings(33))
            list.add(HorizonModel())
            list.addAll(getRandomStrings(33))
            it.adapter = SimpleAdapter(list)
                .addHolderInfo(createSimpleStringHolderInfo())
                .addHolderInfo(
                    HolderInfo(HorizonModel::class.java, R.layout.holder_horizon, HorizonViewHolder::class.java)
                )
            it.layoutManager = LinearLayoutManager(it.context)
        }
    }

}

class HorizonModel

class HorizonViewHolder(v: View): SimpleHolder<HorizonModel>(v) {
    private val jelly = v.findViewById<JellyLayout>(R.id.jelly)
    private val recyclerView = v.findViewById<RecyclerView>(R.id.recycler_view)

    init {
        jelly.also {
            val top = TextView(it.context).also { v ->
                v.setBackgroundColor(0x800FF000.toInt())
                v.textSize = 16F
                v.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
            }
            val bottom = TextView(it.context).also {v ->
                v.setBackgroundColor(0x8000FF00.toInt())
                v.textSize = 16F
                v.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
            }
            val left = TextView(it.context).also { v ->
                v.setBackgroundColor(0x80000FF0.toInt())
                v.textSize = 16F
                v.gravity = Gravity.CENTER_VERTICAL or Gravity.END
            }
            val right = TextView(it.context).also { v ->
                v.setBackgroundColor(0x800000FF.toInt())
                v.textSize = 16F
                v.gravity = Gravity.CENTER_VERTICAL or Gravity.START
            }
            it
                .setTopView(top, 333,  333)
                .setBottomView(bottom, 333, 333)
                .setLeftView(left, 333, 333)
                .setRightView(right, 333, 333)
                .addListeners(object: JellyLayout.Listener {
                    override fun onScrollChanged(region: Int, percent: Float) {
                        when (region) {
                            JellyLayout.REGION_TOP -> top.text = percent.toString()
                            JellyLayout.REGION_BOTTOM -> bottom.text = percent.toString()
                            JellyLayout.REGION_LEFT -> left.text = percent.toString()
                            JellyLayout.REGION_RIGHT -> right.text = percent.toString()
                        }
                    }

                    override fun onReset(region: Int, percent: Float) {
                        if (percent < 80) {
                            return
                        }
                        when (region) {
                            JellyLayout.REGION_TOP -> it.context.toast("Top reset")
                            JellyLayout.REGION_BOTTOM -> it.context.toast("Bottom reset")
                            JellyLayout.REGION_LEFT -> it.context.toast("Left reset")
                            JellyLayout.REGION_RIGHT -> it.context.toast("Right reset")
                        }
                    }
                })
        }
        recyclerView.simpleInit(6, 0x80000000.toInt())
        recyclerView.layoutManager = LinearLayoutManager(v.context, RecyclerView.HORIZONTAL, false)
    }

}