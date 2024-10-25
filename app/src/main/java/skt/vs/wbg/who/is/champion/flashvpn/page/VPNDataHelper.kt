package skt.vs.wbg.who.`is`.champion.flashvpn.page

import android.annotation.SuppressLint
import android.util.Base64
import android.util.Log
import androidx.annotation.Keep
import androidx.core.view.isVisible
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import skt.vs.wbg.who.`is`.champion.flashvpn.BuildConfig
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.OnlineVpnHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.TAG
import java.lang.reflect.Type

object VPNDataHelper {
    var nodeIndex: Int
        get() {
            var node = BaseAppFlash.xkamkaxmak.decodeInt("nodeIndex", 0)
            if (getAllLocaleProfile().size <= node) node = 0
            return node
        }
        set(value) {
            BaseAppFlash.xkamkaxmak.encode("nodeIndex", value)
        }

    var cachePosition = -1

    fun getAllLocaleProfile(): MutableList<LocaleProfile> {
        val list = OnlineVpnHelp.getDataFromTheServer()
        list?.add(0, getFastVpnOnLine(0))
        val fastServerData: MutableList<LocaleProfile>? = OnlineVpnHelp.getDataFastServerData()
        val dataString = SPUtils.getInstance().getString(BaseAppUtils.clockIp, "")

        list?.forEach {
            it.isClock = it.name != "Fast Server"
            if (fastServerData.isNullOrEmpty()) {
                if (dataString.contains(it.onLm_host)) {
                    it.isClock = false
                }
            }
            fastServerData?.forEach {fast->
                if (dataString.contains(it.onLm_host) || fast.onLm_host == it.onLm_host) {
                    it.isClock = false
                }
            }
        }
        // 对 list 进行排序：Fast Server 排第一个，isClock=false 排中间，isClock=true 排最后
        val sortedList = list?.sortedWith(compareBy<LocaleProfile> {
            // 首先将名称为 "Fast Server" 的排在最前面
            it.name != "Fast Server"
        }.thenBy {
            // 其次根据 isClock 排序：isClock=false 排前面
            it.isClock
        })
        return sortedList?.toMutableList() ?: local
    }
    private fun shouldShowClockIcon(profile: LocaleProfile): Boolean {
        val fastServerData: MutableList<LocaleProfile>? = OnlineVpnHelp.getDataFastServerData()
        val dataString = SPUtils.getInstance().getString(BaseAppUtils.clockIp, "")

        if (fastServerData.isNullOrEmpty()) {
            if (dataString.contains(profile.onLm_host)) {
                return false // 如果符合条件，imgColck 不可见
            }
        }

        // 检查是否应该显示 imgClock
        fastServerData?.forEach {
            if (it.onLm_host == profile.onLm_host || dataString.contains(profile.onLm_host)) {
                return false // 如果符合条件，imgColck 不可见
            }
        }

        return true // 否则 imgColck 可见
    }
    private val local = listOf(
        LocaleProfile(
            city = "",
            name = "",
            onLi = "",
            onLm_host = "",
            onLo_Port = 0,
            onLu_password = ""
        )
    ).toMutableList()

    private fun getFastVpnOnLine(type: Int = 0): LocaleProfile {//0:Fast Server;1:Game;2:Video
        val ufVpnBean: MutableList<LocaleProfile>? = OnlineVpnHelp.getDataFastServerData()
        return if (ufVpnBean == null) {
            val data = OnlineVpnHelp.getDataFromTheServer()?.getOrNull(0)
            BaseAppUtils.setLoadData(BaseAppUtils.vpn_ip, data?.onLm_host.toString())
            BaseAppUtils.setLoadData(BaseAppUtils.vpn_city, data?.city.toString())
            LocaleProfile(
                city = data?.city.toString(),
                name = when (type) {
                    0 -> {
                        "Fast Server"
                    }

                    1 -> {
                        "Game"
                    }

                    2 -> {
                        "Video"
                    }

                    else -> {
                        "Fast Server"
                    }
                },
                onLu_password = data?.onLu_password.toString(),
                onLo_Port = data?.onLo_Port ?: 0,
                onLm_host = data?.onLm_host.toString(),
                onLi = data?.onLi.toString()
            )
        } else {
            ufVpnBean.shuffled().first().apply {
                name = when (type) {
                    0 -> {
                        "Fast Server"
                    }

                    1 -> {
                        "Game"
                    }

                    2 -> {
                        "Video"
                    }

                    else -> {
                        "Fast Server"
                    }
                }
            }
        }
    }

    fun getImage(name: String): Int {
        val a = name.trim().replace(" ", "").lowercase()
        when (a) {
            "game" -> return R.drawable.icon_game
            "video" -> return R.drawable.icon_video
            "italy" -> return R.mipmap.flash_image_italy
            "japan" -> return R.mipmap.flash_image_japan
            "koreasouth" -> return R.mipmap.flash_image_koreasouth
            "netherlands" -> return R.mipmap.flash_image_netherlands
            "newzealand" -> return R.mipmap.flash_image_newzealand
            "norway" -> return R.mipmap.flash_image_norway
            "belgium" -> return R.mipmap.flash_image_belgium
            "brazil" -> return R.mipmap.flash_image_brazil
            "canada" -> return R.mipmap.flash_image_canada
            "france" -> return R.mipmap.flash_image_france
            "germany" -> return R.mipmap.flash_image_germany
            "india" -> return R.mipmap.flash_image_india
            "ireland" -> return R.mipmap.flash_image_ireland
            "russianfederation" -> return R.mipmap.flash_image_russianfederation
            "singapore" -> return R.mipmap.flash_image_singapore
            "sweden" -> return R.mipmap.flash_image_sweden
            "switzerland" -> return R.mipmap.flash_image_switzerland
            "unitedarabemirates" -> return R.mipmap.flash_image_unitedarabemirates
            "unitedkingdom" -> return R.mipmap.flash_image_unitedkingdom
            "unitedstates" -> return R.mipmap.flash_image_unitedstates
            "australia" -> return R.mipmap.flash_image_australia
            else -> return R.mipmap.flash_list_icon
        }
    }

    private var remoteVPNNormalString: String? = null
    private var remoteVPNSmartString: String? = null

    @SuppressLint("StaticFieldLeak")
    var remoteConfig: FirebaseRemoteConfig? = null
    private var isGetRemoteString = false

    private var remoteAllList: ArrayList<LocaleProfile>? = null
    private var remoteSmartStringList: ArrayList<String>? = null

    private fun appInitGetVPNFB() {
        remoteConfig = Firebase.remoteConfig
        remoteConfig?.fetchAndActivate()?.addOnSuccessListener {
            remoteVPNNormalString = remoteConfig?.getString("onLwww")
            remoteVPNSmartString = remoteConfig?.getString("onLppp")
            dealFBData()
        }
    }

    private fun dealFBData() {
        if (remoteVPNNormalString?.isNotBlank() == true) {
            try {
                val gson = Gson()
                val base64ListAd = Base64Utils.decode(remoteVPNNormalString)
                val listType: Type = object : TypeToken<ArrayList<LocaleProfile>>() {}.type
                val dataList: ArrayList<LocaleProfile> = gson.fromJson(base64ListAd, listType)
                if (dataList.size > 0) {
                    isGetRemoteString = true
                    remoteAllList = dataList
                }
            } catch (e: Exception) {
                e.printStackTrace()
                remoteVPNNormalString = null
            }
        }
        if (remoteVPNSmartString?.isNotBlank() == true) {
            try {
                val gson = Gson()
                val base64ListAd = Base64Utils.decode(remoteVPNSmartString)
                val listType: Type = object : TypeToken<ArrayList<String>>() {}.type
                val dataList: ArrayList<String> = gson.fromJson(base64ListAd, listType)

                if (dataList.size > 0) {
                    remoteSmartStringList = dataList
                }
            } catch (e: Exception) {
                e.printStackTrace()
                remoteVPNSmartString = null
            }
        }

    }


    fun initVpnFb() {
        if (!BuildConfig.DEBUG) {
            appInitGetVPNFB()
            MainScope().launch {
                delay(4100)
                if (!isGetRemoteString) {
                    while (true) {
                        if (!isGetRemoteString) dealFBData()
                        delay(1900)
                    }
                }
            }
        }
    }

}

object Base64Utils {
    fun decode(encodedString: String?): String {
        return String(Base64.decode(encodedString?.toByteArray(), Base64.DEFAULT))
    }
}

@Keep
data class LocaleProfile(
    @SerializedName("onLu")
    var onLu_password: String = "",
    @SerializedName("onLi")
    var onLi: String = "",
    @SerializedName("onLo")
    var onLo_Port: Int = 0,
    @SerializedName("onLp")
    var name: String = "",
    @SerializedName("onLl")
    var city: String = "",
    @SerializedName("onLm")
    var onLm_host: String = "",
    var isClock: Boolean = false
)
