package skt.vs.wbg.who.`is`.champion.flashvpn.data

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadEndBackAd
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadEndAd
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAd
import skt.vs.wbg.who.`is`.champion.flashvpn.page.EndActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp.putPointFLash

class EndViewModel : ViewModel() {
    fun showEndAd(activity: EndActivity) {
        activity.lifecycleScope.launch {
            delay(200)
            if (activity.lifecycle.currentState != Lifecycle.State.RESUMED) {
                return@launch
            }
            while (isActive) {
                val adEndData = BaseAd.getEndInstance().appAdDataFlash
                if (adEndData == null) {
                    BaseAd.getEndInstance().advertisementLoadingFlash(activity)
                }
                if (adEndData != null) {
                    FlashLoadEndAd.setDisplayEndNativeAdFlash(activity)
                    cancel()
                    break
                }
                delay(500)
            }
        }
    }

    fun showEndScAd(activity: EndActivity) {
        BaseAd.getBackEndInstance().advertisementLoadingFlash(activity)
        "o23".putPointFLash(activity)
        if (FlashLoadEndBackAd.displayBackAdvertisementFlash(1,activity, closeWindowFun = {
                activity.finish()
            }) != 2) {
            activity.finish()
        }
    }
}