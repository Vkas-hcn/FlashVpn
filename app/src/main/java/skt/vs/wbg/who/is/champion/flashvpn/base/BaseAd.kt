package skt.vs.wbg.who.`is`.champion.flashvpn.base

import android.content.Context
import android.util.Log
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustEvent
import com.google.android.gms.ads.AdView
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadBackAd
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadBannerAd
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadConnectAd
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadHomeAd
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadOpenAd
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashAdBean
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp.putPointYep
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
        fun getBackInstance() = instanceHelper.backLoadFlash

        fun getBannerInstance() = instanceHelper.bannerLoadFlash
        private var idCounter = 0
    }

    object InstanceHelper {
        val openLoadFlash = BaseAd()
        val homeLoadFlash = BaseAd()
        val resultLoadFlash = BaseAd()
        val connectLoadFlash = BaseAd()
        val backLoadFlash = BaseAd()
        val bannerLoadFlash = BaseAd()
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
            5 -> "back"
            6 -> "banner"
            else -> ""
        }
    }

    fun getID(adBean: FlashAdBean): String {
        return when (id) {
            1 -> "open+${adBean.onLnugit}"
            2 -> "home+${adBean.onLbibl}"
            3 -> "end+${adBean.onLconcer}"
            4 -> "connect+${adBean.onLnose}"
            5 -> "back+${adBean.onLmemor}"
            6 -> "banner+${adBean.onhhhh}"
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
        val userData = BaseAppUtils.blockAdUsers()
        val blacklistState = BaseAppUtils.blockAdBlacklist()
        if (blacklistState && (instanceName == "connect" || instanceName == "back")) {
            return
        }
        if (!userData && (instanceName == "back" || instanceName == "banner")) {
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
        DataHelp.putPointTimeYep("o30", getID(adData), "yn", context)
        val raolui = BaseAppFlash.mmkvFlash.getBoolean("raoliu", false)
        if (DataHelp.isConnectFun() && !raolui) {
            DataHelp.putPointTimeYep("o33", getID(adData), "yn", context)
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
//            FlashLoadEndAd.loadEndAdvertisementFlash(context, adData)
        }

        adLoadersMap[4] = { context, adData ->
            FlashLoadConnectAd.loadConnectAdvertisementFlash(context, adData)
        }

        adLoadersMap[5] = { context, adData ->
            FlashLoadBackAd.loadBackAdvertisementFlash(context, adData)
        }
        adLoadersMap[6] = { context, adData ->
            FlashLoadBannerAd.loadBannerAdFlash(context, adData)
        }
        return adLoadersMap
    }

    fun beforeLoadLink(yepAdBean: FlashAdBean): FlashAdBean {
        val ipAfterVpnLink = BaseAppUtils.vpn_ip.getLoadStringData()
        val ipAfterVpnCity = BaseAppUtils.vpn_city.getLoadStringData()
        val raoliu = BaseAppFlash.mmkvFlash.getBoolean("raoliu", false)
        if (DataHelp.isConnectFun() && !raoliu) {
            yepAdBean.loadIp = ipAfterVpnLink ?: ""
            yepAdBean.loadCity = ipAfterVpnCity ?: ""
        } else {
            yepAdBean.loadIp = BaseAppUtils.ip_tab_flash.getLoadStringData()
            yepAdBean.loadCity = "null"
        }
        return yepAdBean
    }

    fun afterLoadLink(yepAdBean: FlashAdBean): FlashAdBean {
        val ipAfterVpnLink = BaseAppUtils.vpn_ip.getLoadStringData()
        val ipAfterVpnCity = BaseAppUtils.vpn_city.getLoadStringData()
        val raoliu = BaseAppFlash.mmkvFlash.getBoolean("raoliu", false)
        if (DataHelp.isConnectFun() && !raoliu) {
            yepAdBean.showIp = ipAfterVpnLink ?: ""
            yepAdBean.showTheCity = ipAfterVpnCity ?: ""
        } else {
            yepAdBean.showIp = BaseAppUtils.ip_tab_flash.getLoadStringData()
            yepAdBean.showTheCity = "null"
        }
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
        Log.e(TAG, "getAdTotalCount: ${BaseAppUtils.adShowNum.getLoadIntData()}", )
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
            eventTokenTba?.putPointYep(BaseAppFlash.getInstance())
            Adjust.trackEvent(adjustEvent)
        }
    }
}

