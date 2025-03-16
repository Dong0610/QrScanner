package com.project.qrscanner.ui.permission

import android.os.Build
import com.dong.baselib.api.isApi33orHigher
import com.dong.baselib.listener.OnStateChangeListener
import com.dong.baselib.permission.Permission
import com.dong.baselib.widget.click
import com.dong.baselib.widget.gone
import com.project.qrscanner.app.countGrantCamera
import com.project.qrscanner.app.countGrantFile
import com.project.qrscanner.app.countGrantNoti
import com.project.qrscanner.base.BaseActivity
import com.project.qrscanner.databinding.ActivityPermissionBinding
import com.project.qrscanner.ui.main.MainActivity

class PermissionActivity :
    BaseActivity<ActivityPermissionBinding>(ActivityPermissionBinding::inflate) {
    override fun backPressed() {
        finishAffinity()
    }

    override fun initialize() {

    }

    private val permission by lazy {
        Permission().initialize(this@PermissionActivity)
    }

    override fun ActivityPermissionBinding.onClick() {
        tvContinue.click {
            launchActivity<MainActivity>()
        }
    }

    override fun ActivityPermissionBinding.setData() {

        if (!isApi33orHigher) {
            binding.llNotification.gone()
        }
        if(Build.VERSION.SDK_INT>32){
            binding.lnFileAccess.gone()
        }
        binding.swRequestFile.onStateChangeListener(object : OnStateChangeListener {
            override fun onStateChanged(state: Boolean) {
                if (countGrantFile > 2) {
                    gotToSetting(TypeGoSettings.STORAGE) {
                        binding.swRequestFile.setState(false)
                    }
                } else {
                    requestFileLauncher.launch(permission.storageRequest)
                }
            }
        })
        binding.swRequestCamera.onStateChangeListener(object : OnStateChangeListener {
            override fun onStateChanged(state: Boolean) {
                if (countGrantCamera > 1) {
                    gotToSetting(TypeGoSettings.CAMERA) {
                        binding.swRequestCamera.setState(false)
                    }
                } else {
                    requestCameraLauncher.launch(permission.cameraRequest.toString())
                }
            }
        })
        binding.swRequestNoti.onStateChangeListener(object : OnStateChangeListener {
            override fun onStateChanged(state: Boolean) {
                if (countGrantNoti > 1) {
                    gotToSetting(TypeGoSettings.NOTIFICATION) {
                        binding.swRequestNoti.setState(false)
                    }
                } else {
                    if(Build.VERSION.SDK_INT>=33){
                        requestNotificationLauncher.launch(permission.notificationRequest)
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        binding.swRequestFile.setState(permission.checkGrantedFile)
        binding.swRequestFile.setDisabled(permission.checkGrantedFile)
        binding.swRequestCamera.setState(permission.checkGrantedCamera)
        binding.swRequestCamera.setDisabled(permission.checkGrantedCamera)
        binding.swRequestNoti.setState(permission.checkGrantNotification)
        binding.swRequestNoti.setDisabled(permission.checkGrantNotification)
    }
}