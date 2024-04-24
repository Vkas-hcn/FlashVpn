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
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashAdBean
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.FlashOkHttpUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.TAG
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.logTagFlash
import java.util.Date

object FlashLoadOpenAd {

    private val adBase = BaseAd.getOpenInstance()
    var isFirstLoad: Boolean = false
    private lateinit var adOpenData: FlashAdBean


    fun loadOpenAdFlash(context: Context, adData: FlashAdBean) {
        adOpenData = adBase.beforeLoadLink(adData)
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            adData.onLnugit,
            request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    adBase.isLoadingFlash = false
                    adBase.appAdDataFlash = ad
                    adBase.loadTimeFlash = Date().time
                    ad.setOnPaidEventListener { adValue ->
                        Log.e(TAG, "App open ads start reporting")
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
                    DataHelp.putPointTimeYep(
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
                    DataHelp.putPointTimeYep(
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
        advertisingOpenCallbackFlash(fullScreenFun)
        (adBase.appAdDataFlash as AppOpenAd).show(activity)
        return true
    }
}