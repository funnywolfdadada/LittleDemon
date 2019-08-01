package com.funnywolf.littledemon.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.funnywolf.littledemon.R
import com.funnywolf.littledemon.views.FoldedTextView
import kotlinx.android.synthetic.main.fragment_layout_folded_text.*

class FoldedTextFragment: Fragment() {

    private var currentText: CharSequence = "哒哒哒哒哒哒哒哒哒哒哒哒哒"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_layout_folded_text, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        folded_text_view.setText("""
            关于为什么TRPM8对冷和薄荷醇如此敏感，科学家只能靠推测，因为目前还没有太多确凿的证据。距离Lee和他的同事在《科学》杂志上发表了一篇关于蛋白如何识别薄荷醇分子的文章还没过几个月。
        """.trimIndent(), true)
        folded_text_view.setOnClickListener {
            folded_text_view.fold(!folded_text_view.isFolded())
        }

        folded_text_view_2.setText("""
            关于为什么TRPM8对冷和薄荷醇如此敏感，科学家只能靠推测，因为目前还没有太多确凿的证据。距离Lee和他的同事在《科学》杂志上发表了一篇关于蛋白如何识别薄荷醇分子。
        """.trimIndent(), true)
        folded_text_view_2.setOnClickListener {
            folded_text_view_2.fold(!folded_text_view_2.isFolded())
        }

        folded_text_view_3.setText("""
            关于为什么TRPM8对冷和薄荷醇如此敏感，科学家只能靠推测，因为目前还没有太多确凿的证据。距离Lee和他的同事在《科学》杂志上发表了一篇关于蛋白如何识别薄荷醇分子的文章还没过几个月。
        """.trimIndent(), true)
        folded_text_view_3.setOnClickListener {
            folded_text_view_3.fold(!folded_text_view_3.isFolded())
        }

        folded_text_view_4.setText("""
            关于为什么TRPM8对冷和薄荷醇如此敏感，科学家只能靠推测，因为目前还没有太多确凿的证据。距离Lee和他的同事在《科学》杂志上发表了一篇关于蛋白如何识别薄荷醇分子。
        """.trimIndent(), true)
        folded_text_view_4.setOnClickListener {
            folded_text_view_4.fold(!folded_text_view_4.isFolded())
        }

        folded_text_view_dynamic.text = currentText
        inc.setOnClickListener {
            currentText = "${currentText}哒"
            folded_text_view_dynamic.text = currentText
        }
        dec.setOnClickListener {
            if (currentText.isNotEmpty()) {
                currentText = currentText.subSequence(0, currentText.length - 1)
                folded_text_view_dynamic.text = currentText
            }
        }
    }
}