package net.blwsmartware.qrcodescanner.ui.main

import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.dong.baselib.widget.GradientOrientation
import com.dong.baselib.widget.click
import net.blwsmartware.qrcodescanner.R
import net.blwsmartware.qrcodescanner.app.permission
import net.blwsmartware.qrcodescanner.app.viewModel
import net.blwsmartware.qrcodescanner.base.BaseActivity
import net.blwsmartware.qrcodescanner.databinding.ActivityMainBinding
import net.blwsmartware.qrcodescanner.dialog.DialogQuitApp

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate,true) {

    private lateinit var navController: NavController
    private var lastScreen: Int = 0
    override fun backPressed() {}


    private val quitAppDialog by lazy {
        DialogQuitApp(this){
            finishAffinity()
        }
    }

    override fun fragmentOnBack() {
        super.fragmentOnBack()
        quitAppDialog.show()
    }

    override fun initialize() {
        navController = findNavController(R.id.nav_host_fragment)

        viewModel.currentIndexNav.observe(this@MainActivity) { newScreen ->
            if (newScreen != lastScreen) {
                navController.navigate(newScreen)
                lastScreen = newScreen
            }
            updateBottomNavUI(newScreen)
        }
    }

    override fun ActivityMainBinding.onClick() {
        binding.customBottomNav.llCreate.click {
            if (viewModel.currentIndexNav.value != R.id.homeFragment) {
                viewModel.currentIndexNav.value = R.id.homeFragment
            }
        }
        binding.customBottomNav.llHistory.click {
            if (viewModel.currentIndexNav.value != R.id.historyFragment) {
                viewModel.currentIndexNav.value = R.id.historyFragment
            }
        }
        binding.imgScan.click {
            if (viewModel.currentIndexNav.value != R.id.scanFragment) {
                viewModel.currentIndexNav.value = R.id.scanFragment
            }
        }
    }

    override fun <T> fragmentSendData(key: String, data: T) {
        super.fragmentSendData(key, data)
        if(key=="permission"){
            requestCameraLauncher.launch(permission.cameraRequest)
        }
    }

    override fun ActivityMainBinding.setData() {

    }

    private fun updateBottomNavUI(selectedScreen: Int) {
        val selectedColor = getColor(R.color.gradientS)
        val selectedColorCenter = getColor(R.color.gradientE)
        val selectedColorEnd = getColor(R.color.gradientC)
        val defaultColor = getColor(R.color.color_e0e0e0)

        when (selectedScreen) {
            R.id.homeFragment -> {
                binding.customBottomNav.txtHome.setTextColorGradient(selectedColor, selectedColorEnd, selectedColorCenter)
                binding.customBottomNav.navHome.setGradientIcon(selectedColor, selectedColorEnd, selectedColorCenter, orientation = GradientOrientation.TOP_TO_BOTTOM)
            }
            else -> {
                binding.customBottomNav.txtHome.setTextColorGradient(defaultColor, defaultColor, defaultColor)
                binding.customBottomNav.navHome.setGradientIcon(defaultColor, defaultColor, defaultColor,orientation = GradientOrientation.TOP_TO_BOTTOM)
            }
        }

        when (selectedScreen) {
            R.id.historyFragment -> {
                binding.customBottomNav.txtHistory.setTextColorGradient(selectedColor, selectedColorEnd, selectedColorCenter)
                binding.customBottomNav.navHistory.setGradientIcon(selectedColor, selectedColorEnd, selectedColorCenter)
            }
            else -> {
                binding.customBottomNav.txtHistory.setTextColorGradient(defaultColor, defaultColor, defaultColor)
                binding.customBottomNav.navHistory.setGradientIcon(defaultColor, defaultColor, defaultColor)
            }
        }
    }
}
