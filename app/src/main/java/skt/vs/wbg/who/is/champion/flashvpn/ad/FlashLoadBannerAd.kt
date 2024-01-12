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
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashAdBean
import skt.vs.wbg.who.`is`.champion.flashvpn.page.HomeActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.FlashOkHttpUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.TAG
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.logTagFlash
import java.util.Date

object FlashLoadBannerAd {
    private val adBase = BaseAd.getHomeInstance()
    private var isLoadSuccess: Boolean = false
    fun loadBannerAdFlash(context: Context, adData: FlashAdBean) {
        isLoadSuccess = false
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenWidthDp = screenWidth / displayMetrics.density
        adBase.adView = AdView(context).apply {
            adUnitId = adData.bannerId
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
                // Code to be executed when an ad finishes loading.
                Log.d(TAG, "首页Banner广告加载完成")
                isLoadSuccess = true
                adBase.adView?.setOnPaidEventListener {
                    adBase.adView?.responseInfo.let { res ->
                        if (res != null) {
                            FlashOkHttpUtils().getAdList(
                                context,
                                it,
                                res,
                                "banner",
                                adData
                            )
                        }
                    }
                }
                DataHelp.putPointTimeYep(
                    "o31",
                    "banner+${adData.bannerId}",
                    "yn",
                    context
                )
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Code to be executed when an ad request fails.
                Log.d(TAG, "首页Banner广告加载-onAdFailedToLoad: ${adError.message}")
                isLoadSuccess = false
                val error =
                    """
           domain: ${adError.domain}, code: ${adError.code}, message: ${adError.message}
          """"
                DataHelp.putPointTimeYep(
                    "o32",
                    error,
                    "yn",
                    context
                )
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.d(TAG, "首页Banner广告-onAdOpened")
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.d(TAG, "首页Banner广告-onAdClicked")
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.d(TAG, "首页Banner广告-onAdClosed")
            }
        }
        val adRequest = AdRequest.Builder().build()
        adBase.adView?.loadAd(adRequest)
    }

    fun getAdISLoadSuccess(): Boolean {
        return isLoadSuccess
    }

    fun showBannerAdFlash(activity: HomeActivity) {
        val userData = BaseAppUtils.blockAdUsers()
        if (!userData) {
            return
        }
        val state = activity.lifecycle.currentState == Lifecycle.State.RESUMED
        if (state) {
            // 获取广告视图的父视图，并在需要时移除它
            val parentView = adBase.adView?.parent as? ViewGroup
            parentView?.removeView(adBase.adView)

            // 清除原有的视图
            activity.mBinding.adViewContainer.removeAllViews()
            // 将广告视图添加到容器中
            activity.mBinding.adViewContainer.addView(adBase.adView)
        }
    }


    fun setBannerAdSize(adView: AdView, activity: Activity) {
        // 获取屏幕宽度（以像素为单位）
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels

        // 将屏幕宽度转换为dp
        val screenWidthDp = screenWidth / displayMetrics.density

        // 设置广告尺寸为全宽，高度自适应
        adView.setAdSize(
            AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                activity,
                screenWidthDp.toInt()
            )
        )
        adView.adUnitId = "YOUR_AD_UNIT_ID"

        // 加载广告
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

}