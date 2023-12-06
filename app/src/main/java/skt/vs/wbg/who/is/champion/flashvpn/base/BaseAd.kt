package skt.vs.wbg.who.`is`.champion.flashvpn.base

import android.content.Context
import android.util.Log
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadBackAd
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadConnectAd
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadEndAd
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadHomeAd
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadOpenAd
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashAdBean
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.logTagFlash
import java.util.Date

class BaseAd private constructor() {
    companion object {
        private val instanceHelper = InstanceHelper

        fun getOpenInstance() = instanceHelper.openLoadFlash
        fun getHomeInstance() = instanceHelper.homeLoadFlash
        fun getEndInstance() = instanceHelper.resultLoadFlash
        fun getConnectInstance() = instanceHelper.connectLoadFlash
        fun getBackInstance() = instanceHelper.backLoadFlash

        private var idCounter = 0
    }

    object InstanceHelper {
        val openLoadFlash = BaseAd()
        val homeLoadFlash = BaseAd()
        val resultLoadFlash = BaseAd()
        val connectLoadFlash = BaseAd()
        val backLoadFlash = BaseAd()
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
            else -> ""
        }
    }

    var appAdDataFlash: Any? = null

    var isLoadingFlash = false


    var whetherToShowFlash = false

    var loadTimeFlash: Long = Date().time

    private fun whetherAdExceedsOneHour(loadTime: Long): Boolean =
        Date().time - loadTime < 60 * 60 * 1000

    fun advertisementLoadingFlash(context: Context) {

        if (isLoadingFlash) {
            return
        }
        val userData = BaseAppUtils.blockAdUsers()
        val blacklistState = BaseAppUtils.blockAdBlacklist()
        if (!blacklistState && (instanceName == "connect" || instanceName == "back")) {
            return
        }
        if (!userData && (instanceName == "connect" || instanceName == "back" || instanceName == "home")) {
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
            FlashLoadBackAd.loadBackAdvertisementFlash(context, adData)
        }

        return adLoadersMap
    }

}

