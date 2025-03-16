package com.project.qrscanner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.dong.baselib.builder.fromColor
import com.dong.baselib.lifecycle.change
import com.dong.baselib.widget.GradientOrientation
import com.dong.baselib.widget.click
import com.project.qrscanner.base.BaseAdapter
import com.project.qrscanner.databinding.ItemViewLanguageBinding
import com.project.qrscanner.ui.language.LanguageModel


class LanguageAdapter(var onClick: (String) -> Unit) :
    BaseAdapter<LanguageModel, ItemViewLanguageBinding>() {

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ) = ItemViewLanguageBinding.inflate(inflater, parent, false)

    override fun ItemViewLanguageBinding.bind(item: LanguageModel, position: Int) {
        langName.text = item.name
        currentPosition.change {
            if (it==position) {
                root.apply {
                    setGradientStroke(intArrayOf(fromColor("#5B6CF9"), fromColor("3965FC"), fromColor("a382ea")))
                    setGradientStrokeOrientation(GradientOrientation.TL_BR)
                    setBgColor(fromColor("#141A83FA"))
                }
            } else {
                root.apply {
                    setGradientStroke(intArrayOf(fromColor("#141A83FA"), fromColor("141A83FA"), fromColor("141A83FA")))
                    setGradientStrokeOrientation(GradientOrientation.TL_BR)
                    setBgColor(fromColor("#ffffff"))
                }
            }
        }

        icLang.setImageResource(item.flags)
        root.click {
            currentPosition.value = position
            onClick(item.code)
        }
    }


}
