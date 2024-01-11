package skt.vs.wbg.who.`is`.champion.flashvpn.page

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdSize
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import skt.vs.wbg.who.`is`.champion.flashvpn.BuildConfig
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadOpenAd
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseActivityFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAd
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.databinding.ProgressLayoutBinding
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.FlashOkHttpUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils

class ProgressActivity : BaseActivityFlash<ProgressLayoutBinding>() {
    private var jobOpenAdsFlash: Job? = null
    private var startCateFlash: Job? = null
    override var conetcntLayoutId: Int
        get() = R.layout.progress_layout
        set(value) {}
    var progressInt = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getFileBaseData()
        lifecycleScope.launch(Dispatchers.IO) {
            if (!DataHelp.isConnectFun()) {
                FlashOkHttpUtils().getTbaIp(this@ProgressActivity)
            }
            FlashOkHttpUtils().getSessionList(this@ProgressActivity)
            FlashOkHttpUtils().getVpnData(this@ProgressActivity)
        }
        MainScope().launch {
            for (i in 1..100) {
                if (progressInt != 100) {
                    mBinding.flashProgressBar.progress = i
                }
                delay(120)
            }
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        })
    }

    fun getFileBaseData() {
        startCateFlash = lifecycleScope.launch {
            var isCa = false
            if (!BuildConfig.DEBUG) {
                val auth = Firebase.remoteConfig
                auth.fetchAndActivate().addOnSuccessListener {
                    SPUtils.getInstance()
                        .put(BaseAppUtils.onLguai, auth.getString(BaseAppUtils.onLguai))

                    SPUtils.getInstance()
                        .put(BaseAppUtils.onLglan, auth.getString(BaseAppUtils.onLglan))

                    SPUtils.getInstance()
                        .put(BaseAppUtils.onLdlet, auth.getString(BaseAppUtils.onLdlet))

                    isCa = true
                }
            }
            try {
                withTimeout(4000L) {
                    while (true) {
                        if (!isActive) {
                            break
                        }
                        if (isCa) {
                            loadAdFun()
                            cancel()
                            startCateFlash = null
                        }
                        delay(500)
                    }
                }
            } catch (e: TimeoutCancellationException) {
                cancel()
                startCateFlash = null
                loadAdFun()
            }
        }
    }

    private fun loadAdFun() {
        // 开屏
        BaseAd.getOpenInstance().advertisementLoadingFlash(this)
        loadOpenAd()
        // 首页banner
        BaseAd.getBannerInstance().advertisementLoadingFlash(this)
        // 结果页原生
        BaseAd.getEndInstance().advertisementLoadingFlash(this)
        // 连接插屏
        BaseAd.getConnectInstance().advertisementLoadingFlash(this)
        // 服务器页插屏
        BaseAd.getBackInstance().advertisementLoadingFlash(this)
    }

    private fun loadOpenAd() {
        jobOpenAdsFlash?.cancel()
        jobOpenAdsFlash = null
        jobOpenAdsFlash = lifecycleScope.launch {
            try {
                withTimeout(10000L) {
                    while (isActive) {
                        val showState = FlashLoadOpenAd
                            .displayOpenAdvertisementFlash(this@ProgressActivity, fullScreenFun = {
                                startToMain()
                            })
                        if (showState) {
                            cancel()
                            jobOpenAdsFlash = null
                            progressInt = 100
                            mBinding.flashProgressBar.progress = progressInt
                        }
                        delay(500L)
                    }
                }
            } catch (e: TimeoutCancellationException) {
                cancel()
                jobOpenAdsFlash = null
                progressInt = 100
                mBinding.flashProgressBar.progress = progressInt

                startToMain()
            }
        }
    }

    //跳转到主页
    private fun startToMain() {
        if (BaseAppFlash.isFlashAppBackGround) {
            return
        }
        if (lifecycle.currentState == Lifecycle.State.RESUMED) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        jobOpenAdsFlash?.cancel()
        jobOpenAdsFlash = null
        BaseAppUtils.isStartYep = true
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(300)
            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                if (mBinding.flashProgressBar.progress == 100) {
                    startToMain()
                }
            }
            if (BaseAppUtils.isStartYep) {
                DataHelp.putPointYep("o1startup", this@ProgressActivity)
                BaseAppUtils.isStartYep = false
            }
        }
    }
}