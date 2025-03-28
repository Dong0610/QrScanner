package net.blwsmartware.qrcodescanner.ui.main

import androidx.core.view.isVisible
import com.dong.baselib.widget.click
import net.blwsmartware.qrcodescanner.app.isCreateBarcode
import net.blwsmartware.qrcodescanner.app.isEmailCreateQr
import net.blwsmartware.qrcodescanner.app.isLocationCreateQr
import net.blwsmartware.qrcodescanner.app.isMessageCreateQr
import net.blwsmartware.qrcodescanner.app.isPhoneCreateQr
import net.blwsmartware.qrcodescanner.app.isUrlCreateQr
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
import net.blwsmartware.qrcodescanner.ui.inapp.PremiumActivity

class CreateFragment : BaseFragment<FragmentCreateBinding>(FragmentCreateBinding::inflate, false) {

    override fun backPress() {
        super.backPress()
        fragmentAttach?.fragmentOnBack()
    }

    private fun checkShowIconPremium() {
        binding.apply {
            ivPremiumEmail.isVisible = !isEmailCreateQr
            ivPremiumLocation.isVisible = !isLocationCreateQr
            ivPremiumMessage.isVisible = !isMessageCreateQr
            ivPremiumPhone.isVisible = !isPhoneCreateQr
            ivPremiumBarcode.isVisible = !isCreateBarcode
            ivPremiumUrl.isVisible = !isUrlCreateQr
        }
    }


    override fun FragmentCreateBinding.initView() {
        checkShowIconPremium()
    }

    private fun openPremium() = launchActivity<PremiumActivity>()

    private inline fun launchOrOpenPremium(condition: Boolean, crossinline action: () -> Unit) {
        if (condition) action() else openPremium()
    }

    override fun FragmentCreateBinding.onClick() {
        lnCreateText.click { launchActivity<CreateTextActivity>() }
        lnCreateUrl.click { launchOrOpenPremium(isUrlCreateQr) { launchActivity<CreateUrlActivity>() } }
        lnCreateMessage.click { launchOrOpenPremium(isMessageCreateQr) { launchActivity<CreateMessageActivity>() } }
        lnCreateEmail.click { launchOrOpenPremium(isEmailCreateQr) { launchActivity<CreateEmailActivity>() } }
        lnCreateWifi.click { launchActivity<CreateWifiActivity>() }
        lnCreateLocation.click { launchOrOpenPremium(isLocationCreateQr) { launchActivity<CreateLocationActivity>() } }
        lnCreateContact.click { launchActivity<CreateContactActivity>() }
        lnCreateBarcode.click { launchOrOpenPremium(isCreateBarcode) { launchActivity<CreateBarCodeActivity>() } }
        lnCreatePhone.click { launchOrOpenPremium(isPhoneCreateQr) { launchActivity<CreatePhoneActivity>() } }
        icSettings.click { launchActivity<SettingActivity>() }
    }
}