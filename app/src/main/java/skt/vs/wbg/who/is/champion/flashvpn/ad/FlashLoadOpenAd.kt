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
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.logTagFlash
import java.util.Date

object FlashLoadOpenAd {

    private val adBase = BaseAd.getOpenInstance()
    var isFirstLoad: Boolean = false


     fun loadOpenAdFlash(context: Context, adData: FlashAdBean) {
        Log.d(logTagFlash, "open-开屏广告id=${adData.onLnugit}")
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
                    Log.d(logTagFlash, "open-开屏广告加载成功")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    adBase.isLoadingFlash = false
                    adBase.appAdDataFlash = null
                    if (!isFirstLoad) {
                        adBase.advertisementLoadingFlash(context)
                        isFirstLoad = true
                    }
                    Log.d(logTagFlash, "open-开屏广告加载失败: " + loadAdError.message)
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
                //取消全屏内容
                override fun onAdDismissedFullScreenContent() {
                    Log.d(logTagFlash, "open-关闭开屏内容")
                    adBase.whetherToShowFlash = false
                    adBase.appAdDataFlash = null
                    fullScreenFun()
                }

                //全屏内容无法显示时调用
                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    adBase.whetherToShowFlash = false
                    adBase.appAdDataFlash = null
                    Log.d(logTagFlash, "open-全屏内容无法显示时调用")
                }

                //显示全屏内容时调用
                override fun onAdShowedFullScreenContent() {
                    adBase.appAdDataFlash = null
                    adBase.whetherToShowFlash = true
                    Log.d(logTagFlash, "open--开屏广告展示")
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    Log.d(logTagFlash, "open--点击open广告")
                }
            }
    }


    fun displayOpenAdvertisementFlash(activity: AppCompatActivity,fullScreenFun: () -> Unit): Boolean {
        if (adBase.appAdDataFlash == null) {
            Log.d(logTagFlash, "open--开屏广告加载中。。。")
            return false
        }
        if (adBase.whetherToShowFlash || activity.lifecycle.currentState != Lifecycle.State.RESUMED) {
            Log.d(logTagFlash, "open--前一个开屏广告展示中或者生命周期不对")
            return false
        }
        advertisingOpenCallbackFlash(fullScreenFun)
        (adBase.appAdDataFlash as AppOpenAd).show(activity)
        return true
    }
}