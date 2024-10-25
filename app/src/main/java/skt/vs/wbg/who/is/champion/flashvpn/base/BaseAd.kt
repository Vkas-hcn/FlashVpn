package skt.vs.wbg.who.`is`.champion.flashvpn.base

import android.content.Context
import android.util.Log
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustEvent
import com.google.android.gms.ads.AdView
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadBackEndAd
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadBackListAd
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadBannerAd
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadConnectAd
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadEndAd
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadHomeAd
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadOpenAd
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadRewardedAd
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashAdBean
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp.putPointFLash
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.TAG
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.getLoadIntData
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.getLoadStringData
import java.util.Date


class BaseAd private constructor() {
    companion object {
        private val instanceHelper = InstanceHelper

        fun getOpenInstance() = instanceHelper.openLoadFlash
        fun getHomeInstance() = instanceHelper.homeLoadFlash
        fun getEndInstance() = instanceHelper.resultLoadFlash
        fun getConnectInstance() = instanceHelper.connectLoadFlash
        fun getBackEndInstance() = instanceHelper.backEndLoadFlash
        fun getBackListInstance() = instanceHelper.backListLoadFlash

        fun getBannerInstance() = instanceHelper.bannerLoadFlash

        fun getRewardedInstance() = instanceHelper.rewardedLoadFlash

        private var idCounter = 0
    }

    object InstanceHelper {
        val openLoadFlash = BaseAd()
        val homeLoadFlash = BaseAd()
        val resultLoadFlash = BaseAd()
        val connectLoadFlash = BaseAd()
        val backEndLoadFlash = BaseAd()
        val backListLoadFlash = BaseAd()
        val bannerLoadFlash = BaseAd()
        val rewardedLoadFlash = BaseAd()
    }

    private val id = generateId()

    private fun generateId(): Int {
        idCounter++
        return idCounter
    }

    private val instanceName: String = getInstanceName()

    private fun getInstanceName(): String {
        return when (id) {
            1 -> "open"
            2 -> "home"
            3 -> "end"
            4 -> "connect"
            5 -> "backEnd"
            6 -> "backList"
            7 -> "banner"
            8 -> "rewarded"
            else -> ""
        }
    }

    fun getID(adBean: FlashAdBean): String {
        return when (id) {
            1 -> "open+${adBean.onLnugit}"
            2 -> "home+${adBean.onLbibl}"
            3 -> "end+${adBean.onLconcer}"
            4 -> "connect+${adBean.onLnose}"
            5 -> "backEnd+${adBean.onLdres}"
            6 -> "backList+${adBean.onLmemor}"
            7 -> "banner+${adBean.onhhhh}"
            8 -> "rewarded+${adBean.onLrad}"
            else -> ""
        }
    }

    var appAdDataFlash: Any? = null
    var adView: AdView? = null

    var isLoadingFlash = false


    var whetherToShowFlash = false

    var loadTimeFlash: Long = Date().time

    private fun whetherAdExceedsOneHour(loadTime: Long): Boolean =
        Date().time - loadTime < 60 * 60 * 1000

    fun advertisementLoadingFlash(context: Context) {
        if (isLoadingFlash) {
            Log.d(TAG, "${getInstanceName()}-The ad is loading and cannot be loaded again")
            return
        }
        if (!DataHelp.isConnectFun()) {
            Log.d(TAG, "${getInstanceName()}-The VPN is not connected, the ad cannot be loaded")
            return
        }
        val blacklistState = BaseAppUtils.blockAdBlacklist()
        if (blacklistState && (instanceName == "connect" || instanceName == "backEnd" || instanceName == "backList" || instanceName == "banner")) {
            Log.d(TAG, "黑名单屏蔽：${instanceName}广告，不加载")
            return
        }
        val vpn_ip = BaseAppUtils.vpn_ip.getLoadStringData()
        if ((getLoadIp().isNotEmpty()) && getLoadIp() != vpn_ip) {
            Log.d(
                "TAG",
                "${getInstanceName()}-ip不一致-重新加载-load_ip=" + getLoadIp() + "-now-ip=" + vpn_ip
            )
            whetherToShowFlash = false
            appAdDataFlash = null
            clearLoadIp()
            advertisementLoadingFlash(context)
            return
        }
        when (appAdDataFlash) {
            null -> {
                isLoadingFlash = true
                loadStartupPageAdvertisementFlash(context, BaseAppUtils.getAdJson())
            }
        }
        if (appAdDataFlash != null && !whetherAdExceedsOneHour(loadTimeFlash)) {
            isLoadingFlash = true
            loadStartupPageAdvertisementFlash(context, BaseAppUtils.getAdJson())
        }
    }


    private fun loadStartupPageAdvertisementFlash(context: Context, adData: FlashAdBean) {
        DataHelp.putPointTimeFLash("o30", getID(adData), "yn", context)
        if (DataHelp.isConnectFun()) {
            DataHelp.putPointTimeFLash("o33", getID(adData), "yn", context)
        }
        Log.d(TAG, "${getInstanceName()}-Ads - start loading")
        adLoaders[id]?.invoke(context, adData)
    }

    private val adLoaders = createAdLoadersMap()

    private fun createAdLoadersMap(): Map<Int, (Context, FlashAdBean) -> Unit> {
        val adLoadersMap = mutableMapOf<Int, (Context, FlashAdBean) -> Unit>()

        adLoadersMap[1] = { context, adData ->
            FlashLoadOpenAd.loadOpenAdFlash(context, adData)
        }

        adLoadersMap[2] = { context, adData ->
            FlashLoadHomeAd.loadHomeAdvertisementFlash(context, adData)
        }

        adLoadersMap[3] = { context, adData ->
            FlashLoadEndAd.loadEndAdvertisementFlash(context, adData)
        }

        adLoadersMap[4] = { context, adData ->
            FlashLoadConnectAd.loadConnectAdvertisementFlash(context, adData)
        }

        adLoadersMap[5] = { context, adData ->
            FlashLoadBackEndAd.loadBackAdvertisementFlash(context, adData)
        }

        adLoadersMap[6] = { context, adData ->
            FlashLoadBackListAd.loadBackAdvertisementFlash(context, adData)
        }

        adLoadersMap[7] = { context, adData ->
            FlashLoadBannerAd.loadBannerAdFlash(context, adData)
        }

        adLoadersMap[8] = { context, adData ->
            FlashLoadRewardedAd.loadRewardedAdvertisementFlash(context, adData)
        }
        return adLoadersMap
    }

    fun beforeLoadLink(yepAdBean: FlashAdBean): FlashAdBean {
        val ipAfterVpnLink = BaseAppUtils.vpn_ip.getLoadStringData()
        val ipAfterVpnCity = BaseAppUtils.vpn_city.getLoadStringData()
        yepAdBean.loadIp = ipAfterVpnLink ?: ""
        yepAdBean.loadCity = ipAfterVpnCity ?: ""
        return yepAdBean
    }

    fun afterLoadLink(yepAdBean: FlashAdBean): FlashAdBean {
        val ipAfterVpnLink = BaseAppUtils.vpn_ip.getLoadStringData()
        val ipAfterVpnCity = BaseAppUtils.vpn_city.getLoadStringData()
        yepAdBean.showIp = ipAfterVpnLink ?: ""
        yepAdBean.showTheCity = ipAfterVpnCity ?: ""
        getAdTotalCount()
        return yepAdBean
    }

    private fun getAdTotalCount() {
        if (!BaseAppFlash.is24H) {
            return
        }
        BaseAppUtils.setLoadData(
            BaseAppUtils.adShowNum,
            BaseAppUtils.adShowNum.getLoadIntData() + 1
        )
        Log.e(TAG, "getAdTotalCount: ${BaseAppUtils.adShowNum.getLoadIntData()}")
        val eventTokens = mapOf(
            2 to "8g7h34",
            3 to "96a9zc",
            4 to "vg5dwn",
            5 to "6lio80"
        )
        val eventTokensTbaList = mapOf(
            2 to "onm2",
            3 to "onm3",
            4 to "onm4",
            5 to "onm5"
        )
        val eventToken = eventTokens[BaseAppUtils.adShowNum.getLoadIntData()]
        val eventTokenTba = eventTokensTbaList[BaseAppUtils.adShowNum.getLoadIntData()]

        if (eventToken != null) {
            Log.e(TAG, "adjust-show-ad-num-code: ${eventToken}")
            val adjustEvent = AdjustEvent(eventToken)
            eventTokenTba?.putPointFLash(BaseAppFlash.getInstance())
            Adjust.trackEvent(adjustEvent)
        }
    }

    private fun getLoadIp(): String {
        return when (getInstanceName()) {
            "open" -> BaseAppUtils.openTypeIp
            "home" -> BaseAppUtils.homeTypeIp
            "end" -> BaseAppUtils.endTypeIp
            "connect" -> BaseAppUtils.contTypeIp
            "backEnd" -> BaseAppUtils.backEndTypeIp
            "backList" -> BaseAppUtils.backListTypeIp
            "banner" -> BaseAppUtils.bannerTypeIp
            "rewarded" -> BaseAppUtils.rewardedTypeIp
            else -> {
                ""
            }
        }
    }

    private fun clearLoadIp() {
        when (getInstanceName()) {
            "open" -> BaseAppUtils.openTypeIp = ""
            "home" -> BaseAppUtils.homeTypeIp = ""
            "end" -> BaseAppUtils.endTypeIp = ""
            "connect" -> BaseAppUtils.contTypeIp = ""
            "backEnd" -> BaseAppUtils.backEndTypeIp = ""
            "backList" -> BaseAppUtils.backListTypeIp = ""
            "banner" -> BaseAppUtils.bannerTypeIp = ""
            "rewarded" -> BaseAppUtils.rewardedTypeIp = ""
            else -> {
                ""
            }
        }
    }
}

