package skt.vs.wbg.who.`is`.champion.flashvpn.utils

import android.app.ActivityManager
import android.app.Application
import android.os.Build
import android.os.Process
import android.util.Log
import android.webkit.WebView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashAdBean
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashLogicBean
import skt.vs.wbg.who.`is`.champion.flashvpn.data.FlashUserBean
import skt.vs.wbg.who.`is`.champion.flashvpn.net.FlashCloak
import skt.vs.wbg.who.`is`.champion.flashvpn.page.SPUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.page.VPNDataHelper.initVpnFb

object BaseAppUtils {
    const val logTagFlash = "FlashVPN"
    //refer_data
    const val refer_data = "refer_data"
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
  "onLmemor":"ca-app-pub-3940256099942544/1033173712"
}
    """
    //本地买量数据
    const val local_purchase_data = """
        {
    "onLleav": 2,
    "onLeate": 2,
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
    "onLprob": "2",
    "onLfeli": "2"
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    if (application.packageName != Application.getProcessName()) {
                        WebView.setDataDirectorySuffix(Application.getProcessName())
                    }
                }
                initVpnFb()
                FlashCloak().checkIsLimitCloak()
            }
        }
    }

    //获取本地assets文件夹下的json文件
    fun getAdString(data:String): String {
        return decodeBase64(data)
    }
    fun getPurchaseString(data:String): String {
        return decodeBase64(data)
    }
    fun getAdLogicString(data:String): String {
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
    }

    //屏蔽广告用户
    fun blockAdUsers():Boolean{
        val data = getLogicJson().onLmatt
        when(data){
            "1"->{
                return true
            }
            "2"->{
                return isItABuyingUser()
            }
            "3"->{
                return false
            }
            else->{
                return true
            }
        }
    }
    //黑名单
    fun blockAdBlacklist():Boolean{
        val blackData = SPUtils.getInstance().getBoolean(FlashCloak.IS_BLACK, true)
        when(getLogicJson().onLprob){
            "1"->{
                return !blackData
            }
            "2"->{
                return true
            }
            else->{
                return true
            }
        }
    }
    //是否扰流
    fun spoilerOrNot():Boolean{
        when(getLogicJson().onLfeli){
            "1"->{
                return true
            }
            "2"->{
                return false
            }
            "3"->{
                return !isItABuyingUser()
            }
            else->{
                return false
            }
        }
    }
}