package skt.vs.wbg.who.`is`.champion.flashvpn.data

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadBackEndAd
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadEndAd
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAd
import skt.vs.wbg.who.`is`.champion.flashvpn.page.EndActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp.putPointFLash

class EndViewModel : ViewModel() {
    private var backJob: Job? = null
    fun showEndAd(activity: EndActivity) {
        if (!DataHelp.isConnectFun()) {
            return
        }
        val adEndData = BaseAd.getEndInstance().appAdDataFlash
        if (adEndData == null) {
            BaseAd.getEndInstance().advertisementLoadingFlash(activity)
        }
        activity.lifecycleScope.launch {
            delay(200)
            if (activity.lifecycle.currentState != Lifecycle.State.RESUMED) {
                return@launch
            }
            while (isActive) {
                if (BaseAd.getEndInstance().appAdDataFlash != null) {
                    FlashLoadEndAd.setDisplayEndNativeAdFlash(activity)
                    cancel()
                    break
                }
                delay(500)
            }
        }
    }

    fun showEndScAd(activity: EndActivity) {
        if (activity.mBinding?.showLoad == true) {
            return
        }
        "o23".putPointFLash(activity)
        if (!DataHelp.isConnectFun()) {
            activity.finish()
            return
        }
        showBackAd(activity) {
            shopJob(activity)
            activity.finish()
        }
    }


    private fun showBackAd(activity: EndActivity, nextFun: () -> Unit) {
        backJob?.cancel()
        backJob = null
        backJob = activity.lifecycleScope.launch(Dispatchers.Main) {
            if (FlashLoadBackEndAd.canShowAd(activity) == 0) {
                nextFun()
                return@launch
            }
            BaseAd.getBackEndInstance().advertisementLoadingFlash(activity)
            val startTime = System.currentTimeMillis()
            var elapsedTime: Long
            activity.mBinding.showLoad = true
            try {
                while (isActive) {
                    elapsedTime = System.currentTimeMillis() - startTime
                    if (isActive && elapsedTime >= 4000L) {
                        nextFun()
                        break
                    }
                    if (elapsedTime >= 1000L && FlashLoadBackEndAd.canShowAd(activity) == 2) {
                        shopJob(activity)
                        FlashLoadBackEndAd.displayBackAdvertisementFlash(
                            activity,
                            closeWindowFun = {
                                nextFun()
                            })
                    }
                    delay(500L)
                }
            } catch (e: Exception) {
                nextFun()
            }
        }
    }

    private fun shopJob(activity: EndActivity) {
        backJob?.cancel()
        backJob = null
        activity.mBinding.showLoad = false
    }

}