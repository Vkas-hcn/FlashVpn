package skt.vs.wbg.who.`is`.champion.flashvpn.page

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseActivityFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.data.MainViewModel
import skt.vs.wbg.who.`is`.champion.flashvpn.databinding.MainLayoutBinding
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp.putPointYep
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.getLoadBooleanData
import com.github.mikephil.charting.data.Entry;
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash.Companion.mmkvFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.ChatUtils


class HomeActivity : BaseActivityFlash<MainLayoutBinding>() {
    private val mainViewModel: MainViewModel by viewModels()
    var time: Float = 1f
    private var speedJob: Job? = null
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


        if (!DataHelp.isConnectFun()) {
            mBinding.lottieGuide.visibility = View.VISIBLE
            mBinding.lottieGuide.setAnimation("hahaha.json")
            mBinding.lottieGuide.repeatCount = ValueAnimator.INFINITE
            mBinding.lottieGuide.playAnimation()
            "o1guideexposure".putPointYep(this)
        } else {
            cancelGuideLottie()
        }
        mBinding.lottieGuide.setOnClickListener {
            "o1guidecc".putPointYep(this)
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

        ChatUtils.initChart(mBinding.chart)
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
        val raoLiuTba = BaseAppUtils.raoLiuTba.getLoadBooleanData()
        mmkvFlash.putBoolean("raoliu", data)
        if (!raoLiuTba && !data) {
            "o34".putPointYep(this)
            BaseAppUtils.setLoadData(BaseAppUtils.raoLiuTba, true)
        }
    }

    fun getSpeedData() {
        speedJob?.cancel()
        speedJob = null
        speedJob = lifecycleScope.launch {
            while (isActive) {
                mBinding.downloadText.text = mmkvFlash.decodeString("speed_dow_online", "0 B")
                mBinding.uploadText.text = mmkvFlash.decodeString("speed_up_online", "0 B")
                time++
                if (DataHelp.isConnectFun()) {
                    ChatUtils.simulateDataUpdate(
                        time,
                        mBinding.chart,
                        mBinding.uploadText.text.toString(),
                        mBinding.downloadText.text.toString()
                    )
                }
                delay(500)
            }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.mService?.disconnect()
    }
}