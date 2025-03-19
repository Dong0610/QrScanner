package net.blwsmartware.qrcodescanner.ui.intro

import android.annotation.SuppressLint
import android.os.Parcelable
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.dong.baselib.lifecycle.mutableLiveData
import com.dong.baselib.widget.click
import net.blwsmartware.qrcodescanner.R
import net.blwsmartware.qrcodescanner.base.BaseActivity
import net.blwsmartware.qrcodescanner.databinding.ActivityIntroBinding
import net.blwsmartware.qrcodescanner.ui.permission.PermissionActivity
import kotlinx.android.parcel.Parcelize
import kotlin.math.abs

@Parcelize
data class IntroModel(
    var image: Int = 0, var title: Int = -1, var content: Int = -1,
) : Parcelable

class IntroActivity : BaseActivity<ActivityIntroBinding>(ActivityIntroBinding::inflate, true) {

    private var dots: Array<ImageView>? = null
    private var listIntro: ArrayList<IntroModel> = ArrayList()

    private fun startNextActivity() {
        launchActivity<PermissionActivity>()
        finish()
    }


    private fun changeContentInit(position: Int) {
        for (i in 0..2) {
            if (i == position) dots!![i].setImageResource(R.drawable.ic_intro_s) else dots!![i].setImageResource(
                R.drawable.ic_intro_sn
            )
        }
    }

    override fun backPressed() {
        finishAffinity()
    }

    companion object {
        val isShowNativeFull = mutableLiveData(false)
    }

    var introAdapter: IntroAdapter? = null

    override fun initialize() {
        listIntro.apply {
            add(
                IntroModel(
                    R.drawable.img_intro_1,
                    R.string.intro_title_1,
                    R.string.intro_content_1
                )
            )
            add(
                IntroModel(
                    R.drawable.img_intro_2,
                    R.string.intro_title_2,
                    R.string.intro_content_2
                )
            )
            add(
                IntroModel(
                    R.drawable.img_intro_3,
                    R.string.intro_title_3,
                    R.string.intro_content_3
                )
            )
        }
        dots = arrayOf(
            binding.ivCircle01,
            binding.ivCircle02,
            binding.ivCircle03,
        )
        introAdapter = IntroAdapter(this@IntroActivity)
        introAdapter?.submitList(listIntro)
        binding.viewPager2.adapter = introAdapter
    }

    override fun ActivityIntroBinding.onClick() {

    }

    override fun ActivityIntroBinding.setData() {

        binding.viewPager2.clipToPadding = false
        binding.viewPager2.clipChildren = false
        binding.viewPager2.offscreenPageLimit = 3
        binding.viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_ALWAYS
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(100))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.8f + r * 0.2f
            val absPosition = abs(position)
            page.alpha = 1.0f - (1.0f - 0.3f) * absPosition
        }


        binding.viewPager2.setPageTransformer(compositePageTransformer)
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                changeContentInit(position)

                when (position) {
                    0 -> {

                        binding.btnNext.click {
                            binding.viewPager2.currentItem += 1
                        }
                    }

                    1 -> {
                        binding.btnNext.click {
                            binding.viewPager2.currentItem += 1
                        }
                    }

                    2 -> {
                        binding.btnNext.click {

                            startNextActivity()
                        }


                    }
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        changeContentInit(binding.viewPager2.currentItem)
    }

}
