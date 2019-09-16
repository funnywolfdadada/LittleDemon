package com.funnywolf.littledemon.fragments

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.funnywolf.littledemon.R
import com.funnywolf.littledemon.simpleadapter.SimpleAdapter
import com.funnywolf.littledemon.utils.createSimpleStringHolderInfo
import com.funnywolf.littledemon.utils.getRandomStrings
import kotlinx.android.synthetic.main.fragment_layout_simple_list.*

class ItemDecorationTestFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_layout_simple_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.setBackgroundResource(R.mipmap.bg0)
        recyclerView.adapter = SimpleAdapter.Builder(getRandomStrings(111, false))
            .add(createSimpleStringHolderInfo())
            .build()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(IndexDecorator())
    }

    class IndexDecorator: RecyclerView.ItemDecoration() {
        private val paint = Paint().apply {
            textSize = 50F
        }
        private val background = ColorDrawable(0x80FFFFFF.toInt())
        private val lefOffset = 100

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.left = lefOffset
            if (parent.getChildAdapterPosition(view) != 0) {
                outRect.top = 20
            }
        }

        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            super.onDraw(c, parent, state)
            for (i in 0 until parent.childCount) {
                val child = parent.getChildAt(i)
                drawIndex(c, child, parent.getChildAdapterPosition(child))
            }
        }

        private fun drawIndex(c: Canvas, child: View, index: Int) {
            background.setBounds(0, child.top, child.left, child.bottom)
            background.draw(c)

            val text = "$index"
            c.drawText(text,
                (child.left - paint.measureText(text)) / 2F,
                child.y + child.height / 2 + getIndexTextHeight() / 2,
                paint)
        }

        private fun getIndexTextHeight(): Int {
            val fmi = paint.fontMetricsInt
            return -fmi.ascent
        }

    }

}
