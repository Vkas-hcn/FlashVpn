package skt.vs.wbg.who.`is`.champion.flashvpn.ad

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAd
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashAdBean
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.FlashOkHttpUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.TAG
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.getLoadIntData
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.getLoadStringData
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.logTagFlash
import java.util.Date

object FlashLoadOpenAd {

    private val adBase = BaseAd.getOpenInstance()
    var isFirstLoad: Boolean = false
    private lateinit var adOpenData: FlashAdBean


    fun loadOpenAdFlash(context: Context, adData: FlashAdBean) {
        adOpenData = adBase.beforeLoadLink(adData)
        BaseAppUtils.openTypeIp = BaseAppUtils.vpn_ip.getLoadStringData()
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            adData.onLnugit,
            request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    Log.d(TAG, "open ads start loading success")
                    adBase.isLoadingFlash = false
                    adBase.appAdDataFlash = ad
                    adBase.loadTimeFlash = Date().time
                    ad.setOnPaidEventListener { adValue ->
                        adValue.let {
                            FlashOkHttpUtils().getAdList(
                                context,
                                adValue,
                                ad.responseInfo,
                                "open",
                                adOpenData
                            )
                        }
                    }
                    DataHelp.putPointTimeFLash(
                        "o31",
                        "open+${adData.onLnugit}",
                        "yn",
                        context
                    )
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {

                    adBase.isLoadingFlash = false
                    adBase.appAdDataFlash = null
                    if (!isFirstLoad) {
                        adBase.advertisementLoadingFlash(context)
                        isFirstLoad = true
                    }
                    val error =
                        """
           domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}
          """"
                    Log.d(TAG, "open ads start loading Failed=${error}")
                    DataHelp.putPointTimeFLash(
                        "o32",
                        error,
                        "yn",
                        context
                    )
                }
            }
        )
    }


    private fun advertisingOpenCallbackFlash(fullScreenFun: () -> Unit) {
        if (adBase.appAdDataFlash !is AppOpenAd) {
            return
        }
        (adBase.appAdDataFlash as AppOpenAd).fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    adBase.whetherToShowFlash = false
                    adBase.appAdDataFlash = null
                    fullScreenFun()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    adBase.whetherToShowFlash = false
                    adBase.appAdDataFlash = null
                }

                override fun onAdShowedFullScreenContent() {
                    adBase.appAdDataFlash = null
                    adBase.whetherToShowFlash = true
                    adOpenData = adBase.afterLoadLink(adOpenData)
                    BaseAppFlash.getInstance().adjustPoint("c9lvoj")
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                }
            }
    }


    fun displayOpenAdvertisementFlash(
        activity: AppCompatActivity,
        fullScreenFun: () -> Unit
    ): Boolean {
        if (adBase.appAdDataFlash == null) {
            return false
        }
        if (adBase.whetherToShowFlash || activity.lifecycle.currentState != Lifecycle.State.RESUMED) {
            return false
        }
        val vpnIp = BaseAppUtils.vpn_ip.getLoadStringData()
        if ((BaseAppUtils.openTypeIp.isNotEmpty()) && BaseAppUtils.openTypeIp != vpnIp) {
            Log.d(TAG,  "open-ip不一致-不能展示-load_ip=" + BaseAppUtils.openTypeIp + "-now-ip=" + vpnIp)
            return false
        }
        Log.d(TAG,  "open-ip一致-展示-load_ip=" + BaseAppUtils.openTypeIp + "-now-ip=" + vpnIp)
        advertisingOpenCallbackFlash(fullScreenFun)
        (adBase.appAdDataFlash as AppOpenAd).show(activity)
        return true
    }
}