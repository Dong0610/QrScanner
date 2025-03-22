package net.blwsmartware.qrcodescanner.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dong.baselib.widget.click
import net.blwsmartware.qrcodescanner.base.BaseFragment
import net.blwsmartware.qrcodescanner.databinding.FragmentCreateBinding
import net.blwsmartware.qrcodescanner.ui.create.CreateBarCodeActivity
import net.blwsmartware.qrcodescanner.ui.create.CreateContactActivity
import net.blwsmartware.qrcodescanner.ui.create.CreateEmailActivity
import net.blwsmartware.qrcodescanner.ui.create.CreateLocationActivity
import net.blwsmartware.qrcodescanner.ui.create.CreateMessageActivity
import net.blwsmartware.qrcodescanner.ui.create.CreatePhoneActivity
import net.blwsmartware.qrcodescanner.ui.create.CreateTextActivity
import net.blwsmartware.qrcodescanner.ui.create.CreateUrlActivity
import net.blwsmartware.qrcodescanner.ui.create.CreateWifiActivity

class CreateFragment : BaseFragment<FragmentCreateBinding>(FragmentCreateBinding::inflate,false){
    override fun FragmentCreateBinding.initView() {

    }
    override fun backPress() {
        super.backPress()
        fragmentAttach?.fragmentOnBack()
    }

    override fun FragmentCreateBinding.onClick() {
        lnCreateText.click {
            launchActivity<CreateTextActivity>()
        }
        lnCreateUrl.click {
            launchActivity<CreateUrlActivity>()
        }
        lnCreateMessage.click {
            launchActivity<CreateMessageActivity>()
        }
        lnCreateEmail.click {
            launchActivity<CreateEmailActivity>()
        }
        lnCreateWifi.click {
            launchActivity<CreateWifiActivity>()
        }
        lnCreateLocation.click {
            launchActivity<CreateLocationActivity>()
        }
        lnCreateContact.click {
            launchActivity<CreateContactActivity>()
        }
        lnCreateBarcode.click {
            launchActivity<CreateBarCodeActivity>()
        }

        lnCreatePhone.click {
            launchActivity<CreatePhoneActivity>()
        }

        icSettings.click {
            launchActivity<SettingActivity>()
        }

    }

}