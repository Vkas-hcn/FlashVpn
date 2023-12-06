package skt.vs.wbg.who.`is`.champion.flashvpn.ad

import android.content.Context
import android.graphics.Outline
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAd
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashAdBean
import skt.vs.wbg.who.`is`.champion.flashvpn.page.EndActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.logTagFlash
import java.util.Date

object FlashLoadEndAd {
    private val adBase = BaseAd.getEndInstance()
    fun loadEndAdvertisementFlash(context: Context, adData: FlashAdBean) {
        Log.d(logTagFlash, "end---原生广告id=${adData.onLbibl}")

        val vpnNativeAds = AdLoader.Builder(
            context.applicationContext,
            adData.onLbibl
        )
        val videoOptions = VideoOptions.Builder()
            .setStartMuted(true)
            .build()

        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_LEFT)
            .setMediaAspectRatio(NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_PORTRAIT)
            .build()

        vpnNativeAds.withNativeAdOptions(adOptions)
        vpnNativeAds.forNativeAd {
            adBase.appAdDataFlash = it
            it.setOnPaidEventListener {
                //重新缓存
                BaseAd.getEndInstance().advertisementLoadingFlash(context)
            }
        }
        vpnNativeAds.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                val error =
                    """
           domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}
          """"
                adBase.isLoadingFlash = false
                adBase.appAdDataFlash = null
                Log.d(logTagFlash, "end---加载vpn原生加载失败: $error")
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d(logTagFlash, "end---加载vpn原生广告成功")
                adBase.loadTimeFlash = Date().time
                adBase.isLoadingFlash = false
            }

            override fun onAdOpened() {
                super.onAdOpened()
                Log.d(logTagFlash, "end---点击vpn原生广告")
            }
        }).build().loadAd(AdRequest.Builder().build())
    }


    fun setDisplayEndNativeAdFlash(activity: EndActivity) {
        activity.runOnUiThread {
            val binding = activity.mBinding
            adBase.appAdDataFlash?.let { adData ->
                val state = activity.lifecycle.currentState == Lifecycle.State.RESUMED

                if (adData is NativeAd && !adBase.whetherToShowFlash && state) {
//                    if (!ZELZ.isBlockScreenAds(ZELZ.getLocalVpnBootData().elk_ref)) {
//                        Log.d(logTagFlash,"根据买量屏蔽end广告。。。")
//                        binding.frameLayout2.visibility = View.GONE
//                        return@let
//                    }
                    binding.showAd = 0

                    if (activity.isDestroyed || activity.isFinishing || activity.isChangingConfigurations) {
                        adData.destroy()
                        return@let
                    }
                    val adView = activity.layoutInflater.inflate(
                        R.layout.ad_end,
                        null
                    ) as NativeAdView
                    // 对应原生组件
                    setCorrespondingNativeComponentFlash(adData, adView)
                    binding.adFrame.apply {
                        removeAllViews()
                        addView(adView)
                    }
                    binding.showAd =1
                    adBase.whetherToShowFlash = true
                    adBase.appAdDataFlash = null
                    Log.d(logTagFlash, "end--原生广告--展示")
                }
            }
        }
    }

    private fun setCorrespondingNativeComponentFlash(nativeAd: NativeAd, adView: NativeAdView) {
        adView.mediaView = adView.findViewById(R.id.ad_media)
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)

        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        (adView.headlineView as TextView).text = nativeAd.headline
        nativeAd.mediaContent?.let {
            adView.mediaView?.apply { setImageScaleType(ImageView.ScaleType.CENTER_CROP) }?.mediaContent =
                it
        }
        adView.mediaView?.clipToOutline = true
        adView.mediaView?.outlineProvider = R8()
        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.INVISIBLE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.INVISIBLE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as TextView).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon?.drawable
            )
            adView.iconView?.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)
    }

}