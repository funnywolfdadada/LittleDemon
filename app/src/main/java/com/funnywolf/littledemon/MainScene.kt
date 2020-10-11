package com.funnywolf.littledemon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bytedance.scene.Scene
import com.funnywolf.littledemon.scene.*

/**
 * 主页
 *
 * @author https://github.com/funnywolfdadada
 * @since 2020/3/29
 */
class MainScene : Scene() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.scene_layout_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind(R.id.test_scene, TestScene::class.java)
        bind(R.id.coordinator_layout, CoordinatorLayoutTestScene::class.java)
        bind(R.id.view_drag_test, ViewDragHelperTestScene::class.java)
        bind(R.id.item_decoration, ItemDecorationTestScene::class.java)
        bind(R.id.layout_manager, LayoutManagerTestScene::class.java)
        bind(R.id.transition, TransitionScene::class.java)
    }

    private fun bind(id: Int, sceneClass: Class<out Scene>) {
        findViewById<View>(id)?.setOnClickListener {
            navigationScene?.push(sceneClass)
        }
    }

}
