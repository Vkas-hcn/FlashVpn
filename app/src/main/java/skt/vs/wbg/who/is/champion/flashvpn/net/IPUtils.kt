package skt.vs.wbg.who.`is`.champion.flashvpn.net

import android.app.Dialog
import android.util.Log
import android.view.Gravity
import android.webkit.WebSettings
import androidx.annotation.Keep
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import skt.vs.wbg.who.`is`.champion.flashvpn.BuildConfig
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseActivityFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.page.ProgressActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.page.SPUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp.putPointFLash
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

object IPUtils {
    @Keep
    data class IPInfo(
        var ip: String? = null,
        var country: String? = null,
        var countryCode: String? = null,
        var city: String? = null,
        var region: String? = null,
        var timezone: String? = null,
        var latitude: Double? = null,
        var longitude: Double? = null,
        var postalCode: String? = null,
        var countryShort: String? = null,
        var countryLong: String? = null
    )

    fun fetchIPInfo(url: String): String? {

        return try {

            val urlObj = URL(url)
            val conn = urlObj.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            val responseCode = conn.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = conn.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = reader.readText()
                reader.close()
                response
            } else {
                Log.e(
                    "TAG",
                    "Error fetching IP info=connection.responseCode=${conn.responseCode}"
                )
                null
            }
        } catch (e: Exception) {
            Log.e("TAG", "Error fetching IP info: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    fun getIPInfo() {
        val primaryUrl = "https://api.myip.com/"
        val secondaryUrl = "https://api.infoip.io/"
        val primaryResponse = fetchIPInfo(primaryUrl)
        if (primaryResponse != null) {
            val jsonObject = JSONObject(primaryResponse)
            IPInfo(
                ip = jsonObject.getString("ip"),
                country = jsonObject.getString("country"),
                countryCode = jsonObject.getString("cc")
            )
            SPUtils.getInstance().put(BaseAppUtils.dueIP, jsonObject.getString("cc"))

        } else {
            val secondaryResponse = fetchIPInfo(secondaryUrl)
            if (secondaryResponse != null) {
                val jsonObject = JSONObject(secondaryResponse)
                IPInfo(
                    ip = jsonObject.getString("ip"),
                    country = jsonObject.getString("country_long"),
                    countryCode = jsonObject.getString("country_short"),
                    city = jsonObject.getString("city"),
                    region = jsonObject.getString("region"),
                    timezone = jsonObject.getString("timezone"),
                    latitude = jsonObject.getDouble("latitude"),
                    longitude = jsonObject.getDouble("longitude"),
                    postalCode = jsonObject.getString("postal_code"),
                    countryShort = jsonObject.getString("country_short"),
                    countryLong = jsonObject.getString("country_long")
                )
                SPUtils.getInstance()
                    .put(BaseAppUtils.dueIP, jsonObject.getString("country_short"))
            }
        }
    }

    private var retrofit: Retrofit? = null
    private val netInterceptor = NetInterceptor()

    private val retrofitInstance: Retrofit?
        get() {
            if (retrofit == null) {
                retrofit =
                    Retrofit.Builder().client(createOkhttp()).baseUrl("https://ipapi.co/")
                        .addConverterFactory(GsonConverterFactory.create()).build()
            }
            return retrofit
        }
    var isShowBandedDialog = false

    private fun createOkhttp(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        return builder.connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS)
            .addNetworkInterceptor(netInterceptor).readTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true).build()
    }

    fun checkIp(activity: BaseActivityFlash<*>) {
        activity.lifecycleScope.launch(Dispatchers.IO) {
            getIPInfo()
        }
    }

    fun setIsBanded(activity: BaseActivityFlash<*>): Boolean {
        //开发测试用
        if (BuildConfig.DEBUG) return false
        val countryCode = SPUtils.getInstance().getString(BaseAppUtils.dueIP)

        isShowBandedDialog = if (countryCode.isBlank()) {
            checkIpIsBandedForLanguage()
        } else {
            checkIpIsBanded(countryCode.lowercase())
        }
        if (isShowBandedDialog && activity !is ProgressActivity) {
            showDialog(activity)
            return true
        }
        return false
    }

    private var isShowNum = false
    private fun showDialog(activity: BaseActivityFlash<*>) {
        if (!isShowNum) {
            "o1IPview".putPointFLash(activity)
            isShowNum = true
        }
        val customDialog = Dialog(activity, R.style.AppDialogStyle)
        customDialog.setCancelable(false)
        customDialog.setCanceledOnTouchOutside(false)
        val localLayoutParams = customDialog.window?.attributes
        localLayoutParams?.gravity = Gravity.CENTER
        customDialog.window?.attributes = localLayoutParams
        customDialog.setContentView(R.layout.lllllllll)
        val confirmButton = customDialog.findViewById<AppCompatTextView>(R.id.confirmButton)
        confirmButton.setOnClickListener {
            BaseAppFlash.acFlashList.forEach { it.finish() }
            exitProcess(0)
        }
        customDialog.show()
    }

    fun checkIpIsBanded(string: String?): Boolean {
        Log.e("okhttp banded", "checkIpIsBanded")
        return if (string.isNullOrEmpty()) {
            checkIpIsBandedForLanguage()
        } else {
            when (string) {
                "cn", "hk", "ir", "mo" -> true
                else -> false
            }
        }
    }

    private fun checkIpIsBandedForLanguage(): Boolean {
        Log.e("okhttp banded", "checkIpIsBandedForLanguage")
        return when (Locale.getDefault().language.toLowerCase()) {
            "zh", "fa" -> true

            else -> false
        }
    }


}

class NetInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request: Request = chain.request().newBuilder()
            .addHeader("Connection", "close")
            .addHeader(
                "User-Agent",
                WebSettings.getDefaultUserAgent(BaseAppFlash.getInstance().applicationContext)
            )
            .build()
        return chain.proceed(request)
    }
}

interface ApiService {
    @GET("json")
    fun getIPAddress1(): Call<KKKKKK>
}

@Keep
data class KKKKKK(
    var ip: String, var city: String, var country_code: String
)