package net.blwsmartware.qrcodescanner.ui.intro

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dong.baselib.widget.visible
import net.blwsmartware.qrcodescanner.base.BaseAdapter
import net.blwsmartware.qrcodescanner.databinding.ItemIntroViewBinding

class IntroAdapter(
    var activity: Activity,
) : BaseAdapter<IntroModel, ItemIntroViewBinding>() {

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ) = ItemIntroViewBinding.inflate(inflater, parent, false)

    override fun ItemIntroViewBinding.bind(item: IntroModel, position: Int) {
        viewIntro.visible()
        tvTitle.text = context.getString(item.title)
        tvContent.text = context.getString(item.content)
        context.let {
            Glide.with(it).load(it.getDrawable(item.image)).into(ivIntro)
        }
    }
}