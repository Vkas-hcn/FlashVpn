package skt.vs.wbg.who.`is`.champion.flashvpn.ad

import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAd
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashAdBean
import skt.vs.wbg.who.`is`.champion.flashvpn.page.ConfigActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.FlashOkHttpUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.TAG
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.getLoadStringData
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.logTagFlash
import java.util.Date

object FlashLoadRewardedAd {
    private val adBase = BaseAd.getRewardedInstance()
    private lateinit var adRewardData: FlashAdBean
    private var finishAd = false
    fun loadRewardedAdvertisementFlash(context: Context, adData: FlashAdBean) {
        val adRequest = AdRequest.Builder().build()
        adRewardData = adBase.beforeLoadLink(adData)
        BaseAppUtils.rewardedTypeIp = BaseAppUtils.vpn_ip.getLoadStringData()
        RewardedAd.load(
            context,
            adData.onLrad,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adBase.isLoadingFlash = false
                    adBase.appAdDataFlash = null
                    val error =
                        """
           domain: ${adError.domain}, code: ${adError.code}, message: ${adError.message}
          """"
                    DataHelp.putPointTimeFLash(
                        "o32",
                        error,
                        "yn",
                        context
                    )
                    Log.d(BaseAppUtils.TAG, "reward ads start loading Failed=${error}")
                }

                override fun onAdLoaded(interstitialAd: RewardedAd) {
                    Log.d(BaseAppUtils.TAG, "reward ads start loading success")
                    adBase.loadTimeFlash = Date().time
                    adBase.isLoadingFlash = false
                    adBase.appAdDataFlash = interstitialAd
                    interstitialAd.setOnPaidEventListener { adValue ->
                        FlashOkHttpUtils().getAdList(
                            context, adValue, interstitialAd.responseInfo, "reward",
                            adRewardData
                        )
                    }
                    DataHelp.putPointTimeFLash(
                        "o31",
                        "rewarded+${adData.onLnose}",
                        "yn",
                        context
                    )
                }
            })
    }


    private fun connectScreenAdCallback(closeWindowFun: () -> Unit) {
        (adBase.appAdDataFlash as? RewardedAd)?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdClicked() {
                }

                override fun onAdDismissedFullScreenContent() {
                    Log.e("TAG", "onAdDismissedFullScreenContent:rewarded ")
                    if (finishAd) {
                        closeWindowFun()
                    }
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
                }

                override fun onAdShowedFullScreenContent() {
                    adBase.appAdDataFlash = null
                    // Called when ad is shown.
                    adBase.whetherToShowFlash = true
                    Log.d(logTagFlash, "rewd----show")
                    adRewardData = adBase.afterLoadLink(adRewardData)
                    BaseAppFlash.getInstance().adjustPoint("c9lvoj")
                }
            }
    }


    fun displayRewardedAdvertisementFlash(
        activity: ConfigActivity,
        closeWindowFun: () -> Unit
    ): Int {

        if (adBase.appAdDataFlash == null) {
            return 1
        }

        if (adBase.whetherToShowFlash || activity.lifecycle.currentState != Lifecycle.State.RESUMED) {
            return 1
        }
        val vpnIp = BaseAppUtils.vpn_ip.getLoadStringData()
        if ((BaseAppUtils.rewardedTypeIp.isNotEmpty()) && BaseAppUtils.rewardedTypeIp != vpnIp) {
            Log.d(TAG,  "rewarded-ip不一致-不能展示-load_ip=" + BaseAppUtils.rewardedTypeIp + "-now-ip=" + vpnIp)
            return 2
        }
        Log.d(TAG,  "rewarded-ip一致-展示-load_ip=" + BaseAppUtils.openTypeIp + "-now-ip=" + vpnIp)
        finishAd = false
        connectScreenAdCallback(closeWindowFun)
        activity.lifecycleScope.launch(Dispatchers.Main) {
            if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                (adBase.appAdDataFlash as RewardedAd).let { ad ->
                    Log.d("TAG", "激励广告展示成功:")
                    ad.show(activity) { rewardItem ->
                        val rewardAmount = rewardItem.amount
                        val rewardType = rewardItem.type
                        Log.d("TAG", "激励广告展示成功:rewardAmount${rewardAmount}==${rewardType}")
                        finishAd = true
                    }
                }
            }
        }
        return 2
    }
}