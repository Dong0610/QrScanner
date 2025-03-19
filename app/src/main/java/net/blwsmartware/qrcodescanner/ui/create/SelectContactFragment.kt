package net.blwsmartware.qrcodescanner.ui.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.dong.baselib.lifecycle.change
import com.dong.baselib.lifecycle.get
import com.dong.baselib.lifecycle.mutableLiveData
import com.dong.baselib.lifecycle.set
import com.dong.baselib.widget.click
import com.dong.baselib.widget.fromColor
import com.dong.baselib.widget.gone
import com.dong.baselib.widget.showStateInvisible
import net.blwsmartware.qrcodescanner.R
import net.blwsmartware.qrcodescanner.app.permission
import net.blwsmartware.qrcodescanner.app.viewModel
import net.blwsmartware.qrcodescanner.base.BaseAdapter
import net.blwsmartware.qrcodescanner.base.BaseFragment
import net.blwsmartware.qrcodescanner.databinding.FragmentSelectContactBinding
import net.blwsmartware.qrcodescanner.databinding.ItemSelectContactBinding
import net.blwsmartware.qrcodescanner.model.ContactModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.random.Random

class SelectContactFragment :
    BaseFragment<FragmentSelectContactBinding>(FragmentSelectContactBinding::inflate) {
    override fun backPress() {
        super.backPress()
        closeSelf()
    }

    override fun FragmentSelectContactBinding.initView() {
        if (!permission.checkGrantedContact) {
            contactPermission.launch(permission.contactReadRequest)
        } else {
            queryAllContact()
        }
    }

    var contactPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissions ->
        if (permissions) {
            queryAllContact()
        }

    }

    fun getListColor(): List<Int> {
        return listOf(
            appContext.getColor(com.dong.baselib.R.color.Deep_Pink),
            appContext.getColor(com.dong.baselib.R.color.Red),
            appContext.getColor(R.color.gradientC),
            appContext.getColor(com.dong.baselib.R.color.Mango_Orange),
            appContext.getColor(R.color.gradientE),
            appContext.getColor(com.dong.baselib.R.color.Beer),
            appContext.getColor(com.dong.baselib.R.color.Green_Apple),
            appContext.getColor(com.dong.baselib.R.color.Kelly_Green),
            appContext.getColor(com.dong.baselib.R.color.Yellow),
            appContext.getColor(com.dong.baselib.R.color.Purple_Daffodil),
            appContext.getColor(com.dong.baselib.R.color.Pink_Lemonade),
            appContext.getColor(com.dong.baselib.R.color.Heliotrope_Purple),
            appContext.getColor(com.dong.baselib.R.color.Blue_Orchid),
            appContext.getColor(com.dong.baselib.R.color.Blue_Diamond),
            appContext.getColor(com.dong.baselib.R.color.Sky_Blue)
        )
    }

    val currentContact = mutableLiveData<ContactModel?>(null)

    inner class ContactAdapter(var contact: (ContactModel) -> Unit = {}) :
        BaseAdapter<ContactModel, ItemSelectContactBinding>() {
        override fun createBinding(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemSelectContactBinding.inflate(inflater, parent, false)

        override fun ItemSelectContactBinding.bind(item: ContactModel, position: Int) {
            currentPosition.change {
                if (it == position) {
                    root.stColor(appContext.getColor(R.color.gradientC))
                } else {
                    root.stColor(fromColor("#00000000"))
                }
            }
            if (item.phone == "") txtPhone.gone()
            txtPhone.text = "${item.phone}"
            userName.text = item.name
            val raddom = Random.nextInt(0, getListColor().size)
            srcView.setBgColor(getListColor()[raddom])
            root.click {
                currentPosition.set(position)
                contact(item)
            }
        }
    }

    private val adapter by lazy {
        ContactAdapter {
            currentContact.set(it)
        }
    }

    fun queryAllContact() {
        lifecycleScope.launch {
            viewModel.queryAllContacts(contentResolver = appContext.contentResolver)
                .collectLatest {
                    adapter.submitList(it)
                }
        }

    }


    override fun FragmentSelectContactBinding.onClick() {
        btnBack.click {
            closeSelf()
        }
        btnChoose.showStateInvisible(currentContact)
        btnChoose.click {
            fragmentAttach?.fragmentSendData("contact", currentContact.get())
            closeSelf()
        }
        rcvContact.adapter = adapter
    }

}