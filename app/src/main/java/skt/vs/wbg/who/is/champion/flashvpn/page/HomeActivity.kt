package skt.vs.wbg.who.`is`.champion.flashvpn.page

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseActivityFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.data.MainViewModel
import skt.vs.wbg.who.`is`.champion.flashvpn.databinding.MainLayoutBinding

class HomeActivity : BaseActivityFlash<MainLayoutBinding>() {

    private val mainViewModel: MainViewModel by viewModels()

    override var conetcntLayoutId: Int
        get() = R.layout.main_layout
        set(value) {}

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.init(
            this,
            mBinding.set,
            mBinding.connectImg,
            mBinding.connectAnimate,
            mBinding.listCl,
            mBinding.flashListIcon,
            mBinding.flashListName,
            mBinding.chronometer
        )

        mBinding.lottieGuide.visibility = View.VISIBLE
        mBinding.lottieGuide.setAnimation("hahaha.json")
        mBinding.lottieGuide.repeatCount = ValueAnimator.INFINITE
        mBinding.lottieGuide.playAnimation()
        mBinding.lottieGuide.setOnClickListener {
            cancelGuideLottie()
            mainViewModel.toConnectVerifyNet()
        }
        mBinding.guideMask.setOnClickListener { }
        mBinding.guideMask.setOnTouchListener { _, _ ->
            return@setOnTouchListener true
        }

    }

    var isShowGuide = true

    fun cancelGuideLottie() {
        mBinding.lottieGuide.cancelAnimation()
        mBinding.lottieGuide.isVisible = false
        isShowGuide = false
        mBinding.guideMask.isVisible = false
    }


    override fun onPause() {
        super.onPause()
        mainViewModel.stopToConnectOrDisConnect()
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.activityResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.mService?.disconnect()
    }

}