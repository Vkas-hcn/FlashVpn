package skt.vs.wbg.who.`is`.champion.flashvpn.page

import android.annotation.SuppressLint
import android.util.Base64
import android.util.Log
import androidx.annotation.Keep
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
            if (allLocaleProfiles.size <= node) node = 0
            return node
        }
        set(value) {
            BaseAppFlash.xkamkaxmak.encode("nodeIndex", value)
        }

    var cachePosition = -1

    val allLocaleProfiles: MutableList<LocaleProfile> by lazy {
        getAllLocaleProfile()
    }



    private fun getAllLocaleProfile(): MutableList<LocaleProfile> {
        val list = OnlineVpnHelp.getDataFromTheServer()
        val data = Gson().toJson(list)
        Log.e(TAG, "getAllVpnListData: ${data}")
        list?.add(0, getFastVpnOnLine())
        return list ?: local
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

    private fun getFastVpnOnLine(): LocaleProfile {
        val ufVpnBean: MutableList<LocaleProfile>? = OnlineVpnHelp.getDataFastServerData()
        return if (ufVpnBean == null) {
            val data = OnlineVpnHelp.getDataFromTheServer()?.getOrNull(0)
            BaseAppUtils.setLoadData(BaseAppUtils.vpn_ip, data?.onLm_host.toString())
            BaseAppUtils.setLoadData(BaseAppUtils.vpn_city, data?.city.toString())
            LocaleProfile(
                city = data?.city.toString(),
                name = data?.name.toString(),
                onLu_password = data?.onLu_password.toString(),
                onLo_Port = data?.onLo_Port ?: 0,
                onLm_host = data?.onLm_host.toString(),
                onLi = data?.onLi.toString()
            )
        } else {
            ufVpnBean.shuffled().first().apply {
                name = "Fast Server"
            }
        }
    }

    fun getImage(name: String): Int {
        val a = name.trim().replace(" ", "").lowercase()
        when (a) {
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
    var onLm_host: String = ""
)
