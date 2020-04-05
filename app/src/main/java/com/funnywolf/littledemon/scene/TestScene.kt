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
import com.funnywolf.littledemon.view.*

class TestScene: Scene() {

    private var bottomSheetLayout: BottomSheetLayout? = null
    private var contentView: View? = null

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
            list.add(HorizonModel())
            list.addAll(getRandomStrings(33))
            list.add(HorizonModel())
            list.addAll(getRandomStrings(33))
            it.adapter = SimpleAdapter(list)
                .addHolderInfo(createSimpleStringHolderInfo(0x80000F00.toInt()))
                .addHolderInfo(
                    HolderInfo(HorizonModel::class.java, R.layout.holder_horizon, HorizonViewHolder::class.java)
                )
            it.layoutManager = LinearLayoutManager(it.context)
        }

        findViewById<BottomSheetLayout>(R.id.bottom_sheet)?.also {
            bottomSheetLayout = it

            LayoutInflater.from(view.context).inflate(R.layout.scene_layout_simple_list, it, false)?.also { cv ->
                cv.findViewById<RecyclerView>(R.id.recycler_view)?.also { rv -> rv.simpleInit(55) }
                val lp = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                lp.bottomMargin = 200
                cv.layoutParams = lp
                contentView = cv
            }
        }

        findViewById<TextView>(R.id.tv_test_header)?.setOnClickListener {
            if (bottomSheetLayout?.contentView == null) {
                bottomSheetLayout?.setContentView(
                    contentView?:return@setOnClickListener,
                    400,
                    BOTTOM_SHEET_STATE_COLLAPSED
                )
            } else {
                bottomSheetLayout?.removeContentView()
            }
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
                .listeners.add(object: JellyLayout.Listener {
                    override fun onScrollChanged(region: Int, percent: Float) {
                        when (region) {
                            JELLY_REGION_TOP -> top.text = percent.toString()
                            JELLY_REGION_BOTTOM -> bottom.text = percent.toString()
                            JELLY_REGION_LEFT -> left.text = percent.toString()
                            JELLY_REGION_RIGHT -> right.text = percent.toString()
                        }
                    }

                    override fun onReset(region: Int, percent: Float) {
                        if (percent < 80) {
                            return
                        }
                        when (region) {
                            JELLY_REGION_TOP -> it.context.toast("Top reset")
                            JELLY_REGION_BOTTOM -> it.context.toast("Bottom reset")
                            JELLY_REGION_LEFT -> it.context.toast("Left reset")
                            JELLY_REGION_RIGHT -> it.context.toast("Right reset")
                        }
                    }
                })
        }
        recyclerView.simpleInit(6, 0x800F0000.toInt())
        recyclerView.layoutManager = LinearLayoutManager(v.context, RecyclerView.HORIZONTAL, false)
    }

}
