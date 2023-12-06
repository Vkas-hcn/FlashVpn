package skt.vs.wbg.who.`is`.champion.flashvpn.page

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseActivityFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.data.MainViewModel
import skt.vs.wbg.who.`is`.champion.flashvpn.databinding.MainLayoutBinding
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils

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
        mainViewModel.showConnectLive.observe(this) {
            mainViewModel.showConnecetNextFun(this, it)
        }
        if (!BaseAppUtils.blockAdUsers()) {
            Log.d(BaseAppUtils.logTagFlash, "根据买量屏蔽Home广告。。。")
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
    fun storeSpoilerData(){
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
        mainViewModel.showHomeAd(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.mService?.disconnect()
    }

}