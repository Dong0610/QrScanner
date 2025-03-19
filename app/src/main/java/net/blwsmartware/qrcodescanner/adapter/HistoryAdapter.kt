package net.blwsmartware.qrcodescanner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.dong.baselib.lifecycle.change
import com.dong.baselib.lifecycle.get
import com.dong.baselib.lifecycle.mutableLiveData
import com.dong.baselib.lifecycle.set
import com.dong.baselib.string.currentTimeFormatted
import com.dong.baselib.string.formatTimestamp
import com.dong.baselib.widget.click
import com.dong.baselib.widget.showStateGone
import net.blwsmartware.qrcodescanner.R
import net.blwsmartware.qrcodescanner.base.BaseAdapter
import net.blwsmartware.qrcodescanner.database.model.HistoryApp
import net.blwsmartware.qrcodescanner.databinding.ItemHistoryViewBinding
import net.blwsmartware.qrcodescanner.model.getQrType
import net.blwsmartware.qrcodescanner.model.getTypeRevert
import net.blwsmartware.qrcodescanner.ui.main.HistoryFragment

class HistoryAdapter(var onDelete:(HistoryApp)->Unit={},var onView:(HistoryApp)->Unit={}): BaseAdapter<HistoryApp,ItemHistoryViewBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    )=ItemHistoryViewBinding.inflate(inflater,parent,false)
    private var isChooseItem= mutableLiveData(false)
    fun setUpSelect(action:()->Unit={}){
        isChooseItem.set(!isChooseItem.get()!!)
        action()
    }

    val getStateSelect get() = isChooseItem.get()

    override fun ItemHistoryViewBinding.bind(item: HistoryApp, position: Int) {


        val revertType = getTypeRevert(item.type)
        txtTime.text= formatTimestamp(item.scanTime.toLong())
        txtType.text= context.getString(revertType.title)
        imgIcType.setImageResource(revertType.icon)
        icDelete.click {
            onDelete(item)
        }
        root.click {
            onView(item)
        }
    }
}