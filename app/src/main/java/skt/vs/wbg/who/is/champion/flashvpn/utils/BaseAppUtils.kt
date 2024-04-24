package skt.vs.wbg.who.`is`.champion.flashvpn.utils

import android.app.ActivityManager
import android.app.Application
import android.os.Build
import android.os.Process
import android.util.Log
import android.webkit.WebView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashAdBean
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashLogicBean
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashUserBean
import skt.vs.wbg.who.`is`.champion.flashvpn.net.FlashCloak
import skt.vs.wbg.who.`is`.champion.flashvpn.page.SPUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.page.VPNDataHelper.initVpnFb
import java.net.HttpURLConnection
import java.net.URL
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp.putPointYep
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress

object BaseAppUtils {
    const val TAG = "FlashVPN"
    const val vpn_url = "https://test.onlinenetwork.link/tKyBVzPf/kmFHD/RMA/"
    const val tab_url = "https://test-baste.onlinenetwork.link/summon/wound"
    const val vpn_online = "vpn_online"
    const val ip_tab_flash = "ip_tab_flash"
    const val refer_tab = "refer_tab"
    const val refer_state = "refer_state"
    const val adjust_data = "adjust_data"

    const val logTagFlash = "FlashVPN"
    const val vpn_ip = "vpn_ip"
    const val vpn_city = "vpn_city"
    const val ad_user_state = "ad_user_state"

    //refer_data
    const val refer_data = "refer_data"
    var isStartYep: Boolean = true
    var raoLiuTba = "raoLiuTba"
    var app_pack_name = "a_p_n"
    var app_is_custom = "app_is_custom"
    var app_point_error = "app_point_error"

    //ad
    const val onLguai = "onLguai"

    // 买量
    const val onLglan = "onLglan"

    //类型
    const val onLdlet = "onLdlet"

    // 本地广告数据
    const val local_ad_data = """
{
  "onLnugit":"ca-app-pub-3940256099942544/9257395921",
  "onLbibl":"ca-app-pub-3940256099942544/2247696110",
  "onLconcer":"ca-app-pub-3940256099942544/2247696110",
  "onLnose":"ca-app-pub-3940256099942544/8691691433",
  "onLmemor":"ca-app-pub-3940256099942544/8691691433",
  "onhhhh":"ca-app-pub-3940256099942544/6300978111"
}
    """

    //本地买量数据
    const val local_purchase_data = """
        {
    "onLleav": 1,
    "onLeate": 1,
    "onLmill": 2,
    "onLage": 2,
    "onLiden": 2,
    "onLclem": 2,
    "onLisp": 2
}
    """

    //本地广告逻辑
    const val local_ad_logic = """
{
    "onLmatt": "2",
    "onLprob": "1",
    "onLfeli": "1"
}    """

    fun initApp(application: Application) {
        val myPid = Process.myPid()
        val activityManager =
            application.getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager
        val processInfoList = activityManager.runningAppProcesses
        val packageName = application.packageName
        for (info in processInfoList) {
            if (info!!.pid == myPid && packageName == info.processName) {
                MobileAds.initialize(application) {}
                Firebase.initialize(application)
                FirebaseApp.initializeApp(application)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    if (application.packageName != Application.getProcessName()) {
                        WebView.setDataDirectorySuffix(Application.getProcessName())
                    }
                }
                initVpnFb()
                FlashCloak().checkIsLimitCloak()
                GlobalScope.launch(Dispatchers.IO) {
                    GetAppUtils.getAllLauncherIconPackages(application)
                }
            }
        }
    }

    //获取本地assets文件夹下的json文件
    fun getAdString(data: String): String {
        return decodeBase64(data)
    }

    fun getPurchaseString(data: String): String {
        return decodeBase64(data)
    }

    fun getAdLogicString(data: String): String {
        return decodeBase64(data)
    }

    //base64解密
    private fun decodeBase64(str: String): String {
        return String(android.util.Base64.decode(str, android.util.Base64.DEFAULT))
    }

    fun fromJson(json: String): FlashAdBean {
        val gson = Gson()
        return gson.fromJson(json, FlashAdBean::class.java)
    }

    fun fromUserJson(json: String): FlashUserBean {
        val gson = Gson()
        return gson.fromJson(json, FlashUserBean::class.java)
    }

    //解析JSON数据到FlashLogicBean
    fun fromLogicJson(json: String): FlashLogicBean {
        val gson = Gson()
        return gson.fromJson(json, FlashLogicBean::class.java)
    }

    fun getAdJson(): FlashAdBean {
        val dataJson = SPUtils.getInstance().getString(onLguai).let {
            if (it.isNullOrEmpty()) {
                local_ad_data
            } else {
                getAdString(it)
            }
        }
        return runCatching {
            fromJson(dataJson)
        }.getOrNull() ?: fromJson(local_ad_data)
    }


    fun getUserJson(): FlashUserBean {
        val dataJson = SPUtils.getInstance().getString(onLglan).let {
            if (it.isNullOrEmpty()) {
                local_purchase_data

            } else {
                getPurchaseString(it)
            }
        }
        return runCatching {
            fromUserJson(dataJson)
        }.getOrNull() ?: fromUserJson(local_purchase_data)
    }

    fun getLogicJson(): FlashLogicBean {
        val dataJson = SPUtils.getInstance().getString(onLdlet).let {
            if (it.isNullOrEmpty()) {
                local_ad_logic
            } else {
                getAdLogicString(it)
            }
        }
        return runCatching {
            fromLogicJson(dataJson)
        }.getOrNull() ?: fromLogicJson(local_ad_logic)
    }

    private fun isFacebookUser(): Boolean {
        val data = getUserJson()
        val referrer = SPUtils.getInstance().getString(refer_data)
        val pattern = "fb4a|facebook".toRegex(RegexOption.IGNORE_CASE)
        return (pattern.containsMatchIn(referrer) && data.onLleav == "1")
    }

    fun isItABuyingUser(): Boolean {
        val data = getUserJson()
        val referrer = SPUtils.getInstance().getString(refer_data)
        return isFacebookUser()
                || (data.onLeate == "1" && referrer.contains("gclid", true))
                || (data.onLmill == "1" && referrer.contains("not%20set", true))
                || (data.onLage == "1" && referrer.contains(
            "youtubeads",
            true
        ))
                || (data.onLiden == "1" && referrer.contains("%7B%22", true))
                || (data.onLclem == "1" && referrer.contains("adjust", true))
                || (data.onLisp == "1" && referrer.contains("bytedance", true))
                || adjust_data.getLoadBooleanData()
    }

    //屏蔽广告用户
    fun blockAdUsers(): Boolean {
        val data = getLogicJson().onLmatt
        when (data) {
            "1" -> {
                return true
            }

            "2" -> {
                return isItABuyingUser()
            }

            "3" -> {
                return false
            }

            else -> {
                return true
            }
        }
    }

    //黑名单
    fun blockAdBlacklist(): Boolean {
        val blackData = SPUtils.getInstance().getBoolean(FlashCloak.IS_BLACK, true)
        when (getLogicJson().onLprob) {
            "1" -> {
                return !blackData
            }

            "2" -> {
                return true
            }

            else -> {
                return true
            }
        }
    }

    //是否扰流
    fun spoilerOrNot(): Boolean {
        when (getLogicJson().onLfeli) {
            "1" -> {
                return true
            }

            "2" -> {
                return false
            }

            "3" -> {
                return !isItABuyingUser()
            }

            else -> {
                return false
            }
        }
    }


    fun setLoadData(key: String, value: Any) {
        when (value) {
            is String -> {
                SPUtils.getInstance().put(key, value)
            }

            is Int -> {
                SPUtils.getInstance().put(key, value)
            }

            is Boolean -> {
                SPUtils.getInstance().put(key, value)
            }

            is Float -> {
                SPUtils.getInstance().put(key, value)
            }

            is Long -> {
                SPUtils.getInstance().put(key, value)
            }

            else -> {
                Log.e(TAG, "setLoadData: value is not support")
            }
        }
    }

    fun String.getLoadStringData(): String {
        return SPUtils.getInstance().getString(this)
    }

    fun String.getLoadBooleanData(): Boolean {
        return SPUtils.getInstance().getBoolean(this, false)
    }

    fun String.getLoadIntData(): Int {
        return SPUtils.getInstance().getInt(this)
    }

    fun isNetworkReachable2(): Boolean {
        return try {
            val url = URL("https://www.baidu.com")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 3000 // 设置连接超时时间为3秒
            conn.readTimeout = 3000 // 设置读取超时时间为3秒
            conn.requestMethod = "GET" // 设置请求方法为GET
            conn.connect() // 发起连接

            val responseCode = conn.responseCode
            // 如果响应码是200，表示连接成功
            Log.e(TAG, "表示连接成功")
            responseCode == HttpURLConnection.HTTP_OK
        } catch (e: Exception) {
            // 发生异常，连接失败
            Log.e(TAG, "表示连接失败", )
            false
        }
    }

    fun isNetworkReachable(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("/system/bin/ping -c 1 8.8.8.8") // 执行ping命令
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            process.waitFor() // 等待ping命令执行完成
            process.destroy() // 销毁进程

            // 解析输出，判断是否连接成功
            val result = output.toString()
           val state =  result.contains("1 packets transmitted, 1 received") // 如果输出中包含这行内容，表示ping成功
            Log.e(TAG, "isNetworkReachable: ${state}", )
            return state
        } catch (e: Exception) {
            Log.e(TAG, "isNetworkReachable: ----fasle", )

            false // 发生异常，连接失败
        }
    }


    fun isVpnConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networks = connectivityManager.allNetworks
        for (network in networks) {
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                // 发现了一个 VPN 连接
                return true
            }
        }
        // 未发现 VPN 连接
        return false
    }

    fun o12Fun(context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            Log.e(TAG, "o12Fun: 开始检测")
            val netState = isNetworkReachable()
            if (netState) {
                Log.e(TAG, "o12Fun: 开始检测-1", )
                DataHelp.putPointTimeYep("o12", "1", "net", context)
            } else {
                Log.e(TAG, "o12Fun: 开始检测-2", )
                DataHelp.putPointTimeYep("o12", "2", "net", context)

            }
        }
    }

}