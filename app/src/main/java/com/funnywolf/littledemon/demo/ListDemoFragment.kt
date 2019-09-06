package com.funnywolf.littledemon.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.funnywolf.littledemon.R

/**
 * 列表的 demo，包含常用的列表操作：刷新、加载更多、删除、更新、批量选择等
 *
 * @author funnywolf
 */

/**
 * 列表 demo 页面，处理 UI 相关数据
 */
class ListDemoFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_layout_demo_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
