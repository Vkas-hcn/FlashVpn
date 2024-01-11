package skt.vs.wbg.who.`is`.champion.flashvpn.page

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadConnectAd
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseActivityFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.data.MainViewModel
import skt.vs.wbg.who.`is`.champion.flashvpn.databinding.MainLayoutBinding
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp.putPointYep
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.TAG

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


        if(!DataHelp.isConnectFun()){
            mBinding.lottieGuide.visibility = View.VISIBLE
            mBinding.lottieGuide.setAnimation("hahaha.json")
            mBinding.lottieGuide.repeatCount = ValueAnimator.INFINITE
            mBinding.lottieGuide.playAnimation()
            "o1guideexposure".putPointYep(this)
        }
        mBinding.lottieGuide.setOnClickListener {
            "o1guidecc".putPointYep( this)
            cancelGuideLottie()
            mainViewModel.toConnectVerifyNet()
        }
        mBinding.guideMask.setOnClickListener { }
        mBinding.guideMask.setOnTouchListener { _, _ ->
            return@setOnTouchListener true
        }
        mainViewModel.showConnectLive.observe(this) {
            mainViewModel.showConnecetNextFun(this, it)
        }
        if (!BaseAppUtils.blockAdUsers()) {
            mBinding.showAd = 2
        } else {
            mBinding.showAd = 0
        }
        storeSpoilerData()
    }

    var isShowGuide = true

    fun cancelGuideLottie() {
        mBinding.lottieGuide.cancelAnimation()
        mBinding.lottieGuide.isVisible = false
        isShowGuide = false
        mBinding.guideMask.isVisible = false
    }

    //存储扰流数据
    fun storeSpoilerData() {
        val data = BaseAppUtils.spoilerOrNot()
        BaseAppFlash.mmkvFlash.putBoolean("raoliu", data)
    }

    override fun onPause() {
        super.onPause()
        mainViewModel.stopToConnectOrDisConnect()
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.activityResume()
        mainViewModel.showBannerAd(this)
        "o1frontview".putPointYep(this)
    }

}