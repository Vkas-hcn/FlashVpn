package skt.vs.wbg.who.`is`.champion.flashvpn.tab

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.webkit.WebSettings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.android.installreferrer.api.ReferrerDetails
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.ResponseInfo
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import skt.vs.wbg.who.`is`.champion.flashvpn.BuildConfig
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.data.AdType
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashAdBean
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.TAG
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.getLoadStringData
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

object DataHelp {

    @SuppressLint("HardwareIds")
    private fun createJsonData(context: Context): JSONObject {
        val jsonData = JSONObject()
        // earl
        val earl = JSONObject()

        //system_language
        earl.put("kinky", "${Locale.getDefault().language}_${Locale.getDefault().country}")
        //channel
        earl.put("attica", "")
        //os_version
        earl.put("rug", Build.VERSION.RELEASE)
        //app_version
        earl.put("rarefy", getAppVersion(context))
        jsonData.put("earl", earl)
//---------------------------------------------------------------
        // mnemonic
        val mnemonic = JSONObject()
        //log_id
        mnemonic.put("adoptive", UUID.randomUUID().toString())
        //os
        mnemonic.put("hermite", "assent")
        jsonData.put("mnemonic", mnemonic)
//---------------------------------------------------------------
        // surrey
        val surrey = JSONObject()
        //device_model
        surrey.put("mathews", Build.MODEL)
        //distinct_id
        surrey.put(
            "syrupy",
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        )
        //manufacturer
        surrey.put("widgeon", Build.MODEL)
        //client_ts
        surrey.put("ghost", System.currentTimeMillis())
        jsonData.put("surrey", surrey)
//---------------------------------------------------------------

        // shafer
        val shafer = JSONObject()

        //android_id
        shafer.put(
            "caucasus",
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        )
        //gaid
        shafer.put(
            "jr",
            (runCatching { AdvertisingIdClient.getAdvertisingIdInfo(context).id }.getOrNull() ?: "")
        )
        //network_type
        shafer.put("gully", "")
        //operator
        shafer.put("doghouse", getNetworkInfo(context))
        //bundle_id
        shafer.put("mutate", context.packageName)
        jsonData.put("shafer", shafer)
        return jsonData
    }

    fun getSessionJson(context: Context): String {
        val topLevelJson = createJsonData(context)
        topLevelJson.apply {
            put("cockle", "")
        }
        return topLevelJson.toString()
    }

    fun getInstallJson(rd: ReferrerDetails, context: Context): String {
        val topLevelJson = createJsonData(context)
        val irishman = JSONObject()

        //build
        irishman.put("fugitive", "build/${Build.ID}")

        //referrer_url
        irishman.put("thump", rd.installReferrer)

        //install_version
        irishman.put("segovia", rd.installVersion)

        //user_agent
        irishman.put("fascist", getWebDefaultUserAgent(context))

        //lat
        irishman.put("maier", getLimitTracking(context))

        //referrer_click_timestamp_seconds
        irishman.put("claus", rd.referrerClickTimestampSeconds)

        //install_begin_timestamp_seconds
        irishman.put("hayden", rd.installBeginTimestampSeconds)

        //referrer_click_timestamp_server_seconds
        irishman.put("pet", rd.referrerClickTimestampServerSeconds)

        //install_begin_timestamp_server_seconds
        irishman.put("mantrap", rd.installBeginTimestampServerSeconds)

        //install_first_seconds
        irishman.put("lama", getFirstInstallTime(context))

        //last_update_seconds
        irishman.put("sneeze", getLastUpdateTime(context))

        topLevelJson.put("irishman", irishman)

        return topLevelJson.toString()
    }

    fun getAdJson(
        context: Context, adValue: AdValue,
        responseInfo: ResponseInfo,
        type: String,
        yepAdBean: FlashAdBean
    ): String {
        val topLevelJson = createJsonData(context)
        topLevelJson.apply {
            //ad_pre_ecpm
            put("salt", adValue.valueMicros)
            //currency
            put("chelsea", adValue.currencyCode)
            //ad_network
            put(
                "kelly",
                responseInfo.mediationAdapterClassName
            )
            //ad_source
            put("creamery", "admob")
            //ad_code_id
            put("boyd", getAdType(type).id)
            //ad_pos_id
            put("rosebud", getAdType(type).name)
            //ad_rit_id
            put("password", "")
            //ad_sense
            put("tantric", "")
            //ad_format
            put("taft", getAdType(type).type)
            //precision_type
            put("myth", getPrecisionType(adValue.precisionType))
            //ad_load_ip
            put("mush", yepAdBean.loadIp ?: "")
            //ad_impression_ip
            put("majorca", yepAdBean.showIp ?: "")

            //ad_sdk_ver
            put("impale", responseInfo.responseId)
            put("mcginnis","gander")
        }
        topLevelJson.put("boeotia",JSONObject().apply {
            put("rid_yawn", yepAdBean.loadCity)
            put("rid_fist", yepAdBean.showTheCity)
        })
        return topLevelJson.toString()
    }

    fun getTbaDataJson(context: Context, name: String): String {
        return createJsonData(context).apply {
            put("mcginnis", name)
        }.toString()
    }

    fun getTbaTimeDataJson(
        context: Context,
        time: Any,
        name: String,
        parameterName: String
    ): String {
        val data = JSONObject()
        data.put(parameterName, time)
        return createJsonData(context).apply {
            put("mcginnis", name)
            put("torpor^${parameterName}", time)
        }.toString()
    }

    private fun getAppVersion(context: Context): String {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "Version information not available"
    }


    private fun getNetworkInfo(context: Context): String {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        // 获取网络供应商名称
        val carrierName = telephonyManager.networkOperatorName

        // 获取 MCC 和 MNC
        val networkOperator = telephonyManager.networkOperator
        val mcc = if (networkOperator.length >= 3) networkOperator.substring(0, 3) else ""
        val mnc = if (networkOperator.length >= 5) networkOperator.substring(3) else ""

        return """
        Carrier Name: $carrierName
        MCC: $mcc
        MNC: $mnc
    """.trimIndent()
    }


    private fun getWebDefaultUserAgent(context: Context): String {
        return try {
            WebSettings.getDefaultUserAgent(context)
        } catch (e: Exception) {
            ""
        }
    }

    private fun getLimitTracking(context: Context): String {
        return try {
            if (AdvertisingIdClient.getAdvertisingIdInfo(context).isLimitAdTrackingEnabled) {
                "oblique"
            } else {
                "shiloh"
            }
        } catch (e: Exception) {
            "shiloh"
        }
    }

    private fun getFirstInstallTime(context: Context): Long {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.firstInstallTime / 1000
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 0
    }

    private fun getLastUpdateTime(context: Context): Long {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.lastUpdateTime / 1000
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 0
    }


    private fun getAdType(type: String): AdType {
        var adType = AdType("", "", "", "")
        val adData = BaseAppUtils.getAdJson()
        when (type) {
            "open" -> {
                adType = AdType(adData.onLnugit, "open", "onLnugit", "open")
            }

            "home" -> {
                adType = AdType(adData.onLbibl, "home", "onLbibl", "native")
            }

            "end" -> {
                adType = AdType(adData.onLconcer, "end", "onLconcer", "native")
            }

            "connect" -> {
                adType = AdType(adData.onLnose, "connect", "onLnose", "interstitial")
            }

            "back" -> {
                adType = AdType(adData.onLmemor, "back", "onLmemor", "interstitial")
            }
            "banner" -> {
                adType = AdType(adData.onhhhh, "back", "onLmemor", "banner")
            }
        }
        return adType
    }

    private fun getPrecisionType(precisionType: Int): String {
        return when (precisionType) {
            0 -> {
                "UNKNOWN"
            }

            1 -> {
                "ESTIMATED"
            }

            2 -> {
                "PUBLISHER_PROVIDED"
            }

            3 -> {
                "PRECISE"
            }

            else -> {
                "UNKNOWN"
            }
        }
    }




    fun String.putPointYep(context: Context) {
        FlashOkHttpUtils().getTbaList(context, this)
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "触发埋点----name=${this}")
        } else {
            Firebase.analytics.logEvent(this, null)
        }
    }

    fun putPointTimeYep(name: String, time: Any, parameterName: String, context: Context) {
        FlashOkHttpUtils().getTbaList(context, name, parameterName, time, 1)
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "触发埋点----name=${name}---time=${time}")
        } else {
            Firebase.analytics.logEvent(name, bundleOf(parameterName to time))
        }
    }

    fun isConnectFun(): Boolean {
        return BaseAppFlash.vpnState == "CONNECTED"
    }
}