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
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAd
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashAdBean
import skt.vs.wbg.who.`is`.champion.flashvpn.page.ConfigActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.page.EndActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.page.HomeActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.FlashOkHttpUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.TAG
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.getLoadStringData
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.logTagFlash
import java.util.Date

object FlashLoadBackEndAd {
    private val adBase = BaseAd.getBackEndInstance()
    private lateinit var adBackData: FlashAdBean
    fun loadBackAdvertisementFlash(context: Context, adData: FlashAdBean) {
        val adRequest = AdRequest.Builder().build()
        adBackData = adBase.beforeLoadLink(adData)
        BaseAppUtils.backEndTypeIp = BaseAppUtils.vpn_ip.getLoadStringData()
        InterstitialAd.load(
            context,
            adData.onLdres?:"",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adBase.isLoadingFlash = false
                    adBase.appAdDataFlash = null
                    val error =
                        """
           domain: ${adError.domain}, code: ${adError.code}, message: ${adError.message}
          """"
                    Log.d(TAG, "backEnd-The ad failed to load:$error ")

                    DataHelp.putPointTimeFLash(
                        "o32",
                        error,
                        "yn",
                        context
                    )
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    adBase.loadTimeFlash = Date().time
                    adBase.isLoadingFlash = false
                    adBase.appAdDataFlash = interstitialAd
                    Log.d(TAG, "backEnd-The ad loads successfully: ")
                    interstitialAd.setOnPaidEventListener { adValue ->
                        FlashOkHttpUtils().getAdList(
                            context,
                            adValue,
                            interstitialAd.responseInfo,
                            "backEnd",
                            adBackData
                        )
                    }
                    DataHelp.putPointTimeFLash(
                        "o31",
                        "backEnd+${adData.onLmemor}",
                        "yn",
                        context
                    )
                }
            })
    }

    private fun backScreenAdCallback(closeWindowFun: () -> Unit) {
        (adBase.appAdDataFlash as? InterstitialAd)?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdClicked() {
                }

                override fun onAdDismissedFullScreenContent() {
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
                }

                override fun onAdShowedFullScreenContent() {
                    adBase.appAdDataFlash = null
                    // Called when ad is shown.
                    adBase.whetherToShowFlash = true
                    Log.d(logTagFlash, "backEnd----show")
                    adBackData = adBase.afterLoadLink(adBackData)
                    BaseAppFlash.getInstance().adjustPoint("c9lvoj")
                }
            }
    }
    fun canShowAd(
        activity: AppCompatActivity,
    ): Int {
        val blacklistState = BaseAppUtils.blockAdBlacklist()
        if (blacklistState) {
            Log.d(TAG, "黑名单屏蔽：backEnd广告，不显示")
            return 0
        }

        if (adBase.appAdDataFlash == null) {
            return 1
        }

        if (adBase.whetherToShowFlash || activity.lifecycle.currentState != Lifecycle.State.RESUMED) {
            return 1
        }
        val vpnIp = BaseAppUtils.vpn_ip.getLoadStringData()
        if ((BaseAppUtils.backEndTypeIp.isNotEmpty()) && BaseAppUtils.backEndTypeIp != vpnIp) {
            Log.d(
                TAG,
                "backList-ip不一致-不能展示-load_ip=" + BaseAppUtils.backEndTypeIp + "-now-ip=" + vpnIp
            )
            return 0
        }
        return 2
    }

    fun displayBackAdvertisementFlash(
        activity: AppCompatActivity,
        closeWindowFun: () -> Unit
    ): Int {
        val vpnIp = BaseAppUtils.vpn_ip.getLoadStringData()
        Log.d(TAG,  "backEnd-ip一致-展示-load_ip=" + BaseAppUtils.backEndTypeIp + "-now-ip=" + vpnIp)
        backScreenAdCallback(closeWindowFun)
        activity.lifecycleScope.launch(Dispatchers.Main) {
            if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                (adBase.appAdDataFlash as InterstitialAd).show(activity)
            }
        }
        return 2
    }
}