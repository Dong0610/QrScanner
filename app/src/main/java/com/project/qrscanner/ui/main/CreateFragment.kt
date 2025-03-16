package com.project.qrscanner.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dong.baselib.widget.click
import com.project.qrscanner.base.BaseFragment
import com.project.qrscanner.databinding.FragmentCreateBinding
import com.project.qrscanner.ui.create.CreateTextActivity
import com.project.qrscanner.ui.create.CreateUrlActivity

class CreateFragment : BaseFragment<FragmentCreateBinding>(FragmentCreateBinding::inflate,false){
    override fun FragmentCreateBinding.initView() {

    }

    override fun FragmentCreateBinding.onClick() {
        lnCreateText.click {
            launchActivity<CreateTextActivity>()
        }
        lnCreateUrl.click {
            launchActivity<CreateUrlActivity>()
        }
    }

}