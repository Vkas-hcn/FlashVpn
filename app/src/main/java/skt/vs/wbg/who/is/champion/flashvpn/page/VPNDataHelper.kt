package skt.vs.wbg.who.`is`.champion.flashvpn.page

import android.annotation.SuppressLint
import android.util.Base64
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

    val allLocaleProfiles: ArrayList<LocaleProfile> by lazy {
        getAllLocaleProfile()
    }

    private fun getAllLocaleProfile(): ArrayList<LocaleProfile> {
        if ((remoteAllList?.size ?: 0) > 0) {
            val dataList = remoteAllList!!
            return if ((remoteSmartStringList?.size ?: 0) > 0) {
                val a: LocaleProfile? = dataList.findLast {
                    remoteVPNSmartString?.get(0).toString() == it.onLm_host
                }
                if (a != null)
                    dataList.apply { this.add(a) }
                else
                    dataList.apply {
                        add(0, getSmart())
                    }
            } else {
                dataList.apply {
                    add(0, getSmart())
                }
            }
        } else
            return getFileLocaleProfile().apply {
                add(0, getSmart())
            }
    }

    fun getSmart(): LocaleProfile {
        val gson = Gson()
        val data = gson.fromJson(Base64Utils.decode(jsonSmart), LocaleProfile::class.java)
        return data
    }


    private var jsonLocaleData = """
      IFsKICAgICAgICAgIHsKICAgICAgICAgICAgIm9uTHUiOiAiblRDUkRpZnhQNGdlYncycEZRTjc9IiwKICAgICAgICAgICAgIm9uTGki
      OiAiYWVzLTI1Ni1nY20iLAogICAgICAgICAgICAib25MbyI6ICIzMzExIiwKICAgICAgICAgICAgIm9uTHAiOiAiVW5pdGVkIFN0YXR
      lcyIsCiAgICAgICAgICAgICJvbkxsIjogIlNlYXR0bGUiLAogICAgICAgICAgICAib25MbSI6ICI2Ni40Mi42NC41NSIKICAgICAgICAgIH0se
      wogICAgICAgICAgICAib25MdSI6ICJuVENSRGlmeFA0Z2VidzJwRlFONz0iLAogICAgICAgICAgICAib25MaSI6ICJhZXMtMjU2LWdjbSIsCiAg
      ICAgICAgICAgICJvbkxvIjogIjMzMTEiLAogICAgICAgICAgICAib25McCI6ICJKYXBhbiIsCiAgICAgICAgICAgICJvbkxsIjogIlRva3lv
      IiwKICAgICAgICAgICAgIm9uTG0iOiAiNDUuNzYuMjE0Ljk0IgogICAgIC
      AgICAgfSx7CiAgICAgICAgICAgICJvbkx1IjogIm5UQ1JEaWZ4UDRnZWJ3MnBGUU43PSIsCiAgICAgICAgICAgICJvbkxpIjogImF
      lcy0yNTYtZ2NtIiwKICAgICAgICAgICAgIm9uTG8iOiAiMzMxMSIsCiAgICAgICAgICAgICJvbkxwIjogIlVuaXRlZGtpbmdkb
      20iLAogICAgICAgICAgICAib25MbCI6ICJMb25kb24iLAogICAgICAgICAgICAib25MbSI6ICI5NS4xNzkuMTk2LjEzMCIKICAgICAgI
      CAgIH0sewogICAgICAgICAgICAib25MdSI6ICJuVENSRGlmeFA0Z2VidzJwRlFONz0iLAogICAgICAgICAgICAib25MaSI6ICJhZXMtMjU2LW
      djbSIsCiAgICAgICAgICAgICJvbkxvIjogIjMzMTEiLAogICAgICAgICAgICAib25McCI6ICJDYW5hZGEiLAogICAgICAgICAgICAib25MbCI
      6ICJUb3JvbnRvIiwKICAgICAgICAgICAgIm9uTG0iOiAiMTU1LjEzOC4xMzguMTM3ICIKICAgICAgICAgIH0KICAgICAgICBd
    """.trimIndent()



    private var jsonSmart =
        "ewogICAgICAgICAgICAib25MdSI6ICJuVENSRG" +
                "lmeFA0Z2VidzJwRlFONz0iLAogICAgICAgICAgICAib25MaSI6ICJhZXM" +
                "tMjU2LWdjbSIsCiAgICAgICAgICAgICJvbkxvIjogIjMzMTEiLAogICAgICAgICAgICAib25McCI" +
                "6ICJGYXN0IFNlcnZlciIsCiAgICAgICAgICAgICJvbkxsIjo" +
                "gIiIsCiAgICAgICAgICAgICJvbkxtIjogIjY2LjQyLjY0LjU1IgogICAgICAgICAgfQ==".trimIndent()


    fun getFileLocaleProfile(): ArrayList<LocaleProfile> {
        val gson = Gson()
        val listType: Type = object : TypeToken<ArrayList<LocaleProfile>>() {}.type
        return gson.fromJson(Base64Utils.decode(jsonLocaleData), listType)
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
