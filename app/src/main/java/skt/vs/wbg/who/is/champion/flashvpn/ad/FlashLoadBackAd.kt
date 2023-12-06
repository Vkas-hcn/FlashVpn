package skt.vs.wbg.who.`is`.champion.flashvpn.ad

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAd
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashAdBean
import skt.vs.wbg.who.`is`.champion.flashvpn.page.HomeActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.logTagFlash
import java.util.Date

object FlashLoadBackAd {
    private val adBase = BaseAd.getBackInstance()
    fun loadBackAdvertisementFlash(context: Context, adData: FlashAdBean) {
        val adRequest = AdRequest.Builder().build()
        Log.d(
            logTagFlash,
            "back--插屏广告id=${adData.onLnose}"
        )
        InterstitialAd.load(
            context,
            adData.onLnose,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adError.toString().let { Log.d(logTagFlash, "back---连接插屏加载失败=$it") }
                    adBase.isLoadingFlash = false
                    adBase.appAdDataFlash = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    adBase.loadTimeFlash = Date().time
                    adBase.isLoadingFlash = false
                    adBase.appAdDataFlash = interstitialAd
                    Log.d(logTagFlash, "back---连接插屏加载成功")
                }
            })
    }

    private fun backScreenAdCallback(closeWindowFun: () -> Unit) {
        (adBase.appAdDataFlash as? InterstitialAd)?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    Log.d(logTagFlash, "back插屏广告点击")
                }

                override fun onAdDismissedFullScreenContent() {
                    Log.d(logTagFlash, "关闭back插屏广告=")
                    closeWindowFun()
                    adBase.appAdDataFlash = null
                    adBase.whetherToShowFlash = false
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    // Called when ad fails to show.
                    Log.d(logTagFlash, "Ad failed to show fullscreen content.")
                    adBase.appAdDataFlash = null
                    adBase.whetherToShowFlash = false
                }

                override fun onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.e("TAG", "Ad recorded an impression.")
                }

                override fun onAdShowedFullScreenContent() {
                    adBase.appAdDataFlash = null
                    // Called when ad is shown.
                    adBase.whetherToShowFlash = true
                    Log.d(logTagFlash, "back----show")
                }
            }
    }


    fun displayBackAdvertisementFlash(
        activity: AppCompatActivity,
        closeWindowFun: () -> Unit
    ): Int {
        val userData = BaseAppUtils.blockAdUsers()
        val blacklistState = BaseAppUtils.blockAdBlacklist()
        if (!blacklistState) {
            Log.d(logTagFlash, "根据黑名单屏蔽Back广告。。。")
            return 0
        }
        if (!userData) {
            Log.d(logTagFlash, "根据买量屏蔽Back广告。。。")
            return 0
        }

        if (adBase.appAdDataFlash == null) {
            Log.d(logTagFlash, "back--插屏广告加载中")
            return 1
        }

        if (adBase.whetherToShowFlash || activity.lifecycle.currentState != Lifecycle.State.RESUMED) {
            Log.d(logTagFlash, "back--前一个插屏广告展示中或者生命周期不对")
            return 1
        }
        backScreenAdCallback(closeWindowFun)
        activity.lifecycleScope.launch(Dispatchers.Main) {
            if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                (adBase.appAdDataFlash as InterstitialAd).show(activity)
            }
        }
        return 2
    }
}