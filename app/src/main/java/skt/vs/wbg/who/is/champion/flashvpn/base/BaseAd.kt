package skt.vs.wbg.who.`is`.champion.flashvpn.base

import android.content.Context
import android.util.Log
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.logTagFlash

class BaseAd private constructor() {
    companion object {
        private val instanceHelper = InstanceHelper

        fun getOpenInstance() = instanceHelper.openLoadFlash
        fun getHomeInstance() = instanceHelper.homeLoadFlash
        fun getResultInstance() = instanceHelper.resultLoadFlash
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
            3 -> "result"
            4 -> "connect"
            5 -> "back"
            else -> ""
        }
    }

    var appAdDataFlash: Any? = null

    var isLoadingFlash = false

    var loadTimeFlash: Long = getCurrentTimeMillis()

    var whetherToShowFlash = false

    var adIndexFlash = 0


    fun advertisementLoadingFlash(context: Context) {

        if (isLoadingFlash) {
            Log.d(logTagFlash, "$instanceName--广告加载中，不能再次加载")
            return
        }
//        if ((getInstanceName() == "back" || getInstanceName() == "connect")
//            && ZELZ.isBuyBlackListBan()) {
//            LogUtils.eTag(logTagFlash,"黑名单屏蔽用户不加载${getInstanceName()}广告")
//            return
//        }
//        if ((getInstanceName() == "back" || getInstanceName() == "connect"||getInstanceName() == "home")
//            && ZELZ.isBuyQuantityBan()) {
//            LogUtils.eTag(logTagFlash,"买量屏蔽用户不加载${getInstanceName()}广告")
//            return
//        }
        when {
            appAdDataFlash == null -> {
                isLoadingFlash = true
                Log.d(logTagFlash, "$instanceName--广告开始加载")
                loadStartupPageAdvertisementFlash(context, ZELZ.getAdServerDataFlash())
            }
            appAdDataFlash != null && !whetherAdExceedsOneHour(loadTimeFlash) -> {
                isLoadingFlash = true
                LogUtils.d(logTagFlash, "$instanceName--广告过期重新加载")
                loadStartupPageAdvertisementFlash(context, ZELZ.getAdServerDataFlash())
            }
        }
    }

    private fun whetherAdExceedsOneHour(loadTime: Long): Boolean =
        getCurrentTimeMillis() - loadTime < 60 * 60 * 1000

    private fun loadStartupPageAdvertisementFlash(context: Context, adData: FlashAdBean) {
        adLoaders[id]?.invoke(context, adData)
    }

    private val adLoaders = createAdLoadersMap()

    private fun createAdLoadersMap(): Map<Int, (Context, FlashAdBean) -> Unit> {
        val adLoadersMap = mutableMapOf<Int, (Context, FlashAdBean) -> Unit>()

        adLoadersMap[1] = { context, adData ->
            val adType = adData.open.getOrNull(adIndexFlash)?.Gu0_entrpe
            if (adType == "calet") {
                FlashLoadOpenAd.loadStartInsertAdFlash(context, adData)
            } else {
                FlashLoadOpenAd.loadOpenAdvertisementFlash(context, adData)
            }
        }

        adLoadersMap[2] = { context, adData ->
            FlashLoadHomeAd.loadHomeAdvertisementFlash(context, adData)
        }

        adLoadersMap[3] = { context, adData ->
            FlashLoadResultAd.loadResultAdvertisementFlash(context, adData)
        }

        adLoadersMap[4] = { context, adData ->
            FlashLoadConnectAd.loadConnectAdvertisementFlash(context, adData)
        }

        adLoadersMap[5] = { context, adData ->
            FlashLoadBackAd.loadBackAdvertisementFlash(context, adData)
        }

        return adLoadersMap
    }

    private fun getCurrentTimeMillis(): Long = Date().time
}

