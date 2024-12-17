package skt.vs.wbg.who.`is`.champion.flashvpn.ad

import android.app.Activity
import android.content.Context
import android.graphics.Outline
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAd
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashAdBean
import skt.vs.wbg.who.`is`.champion.flashvpn.page.HomeActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.FlashOkHttpUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.TAG
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.getLoadIntData
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.getLoadStringData
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.logTagFlash
import java.util.Date

object FlashLoadBannerAd {
    private val adBase = BaseAd.getHomeInstance()
    private var isLoadSuccess: Boolean = false
    private lateinit var adBackData: FlashAdBean

    fun loadBannerAdFlash(context: Context, adData: FlashAdBean) {
       adBackData = adBase.beforeLoadLink(adData)
        BaseAppUtils.bannerTypeIp = BaseAppUtils.vpn_ip.getLoadStringData()
        isLoadSuccess = false
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenWidthDp = screenWidth / displayMetrics.density
        adBase.adView = AdView(context).apply {
            adUnitId = adData.onhhhh
            setAdSize(
                AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                    context,
                    screenWidthDp.toInt()
                )
            )
            adSize
        }
        adBase.adView?.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d(TAG, "banner-The ad loads successfully: ")
                adBase.isLoadingFlash = false
                isLoadSuccess = true
                adBase.adView?.setOnPaidEventListener {
                    adBase.adView?.responseInfo.let { res ->
                        if (res != null) {
                            FlashOkHttpUtils().getAdList(
                                context,
                                it,
                                res,
                                "banner",
                                adBackData
                            )
                        }
                    }
                }
                DataHelp.putPointTimeFLash(
                    "o31",
                    "banner+${adData.onhhhh}",
                    "yn",
                    context
                )
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                adBase.isLoadingFlash = false
                isLoadSuccess = false
                val error =
                    """
           domain: ${adError.domain}, code: ${adError.code}, message: ${adError.message}
          """"
                Log.d(TAG, "banner-The ad failed to load:$error ")
                DataHelp.putPointTimeFLash(
                    "o32",
                    error,
                    "yn",
                    context
                )
            }

            override fun onAdOpened() {
                Log.e(
                    "TAG",
                    "banner-ip一致-展示-load_ip="
                )
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }
        val adRequest = AdRequest.Builder().build()
        adBase.adView?.loadAd(adRequest)
    }

    fun getAdISLoadSuccess(): Boolean {
        return isLoadSuccess
    }

    fun showBannerAdFlash(activity: HomeActivity) {
        val vpnIp = BaseAppUtils.vpn_ip.getLoadStringData()
        Log.e(
            "TAG",
            "banner-ip一致-展示-load_ip=" + BaseAppUtils.bannerTypeIp + "-now-ip=" + vpnIp
        )
        val state = activity.lifecycle.currentState == Lifecycle.State.RESUMED
        if (state) {
            val parentView = adBase.adView?.parent as? ViewGroup
            parentView?.removeView(adBase.adView)
            activity.mBinding.adViewContainer.removeAllViews()
            activity.mBinding.adViewContainer.addView(adBase.adView)
        }
        adBackData = adBase.afterLoadLink(adBackData)
        BaseAppFlash.getInstance().adjustPoint("c9lvoj")
    }
}