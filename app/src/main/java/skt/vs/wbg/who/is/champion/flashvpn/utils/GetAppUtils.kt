package skt.vs.wbg.who.`is`.champion.flashvpn.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.data.AppInfo
import skt.vs.wbg.who.`is`.champion.flashvpn.page.PingActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.getLoadBooleanData
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.getLoadStringData
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.URL
import java.util.Locale

object GetAppUtils {
    private var appList: MutableList<AppInfo> = mutableListOf()

    fun getAllLauncherIconPackages(context: Context) {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val resolveInfos =
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        appList = resolveInfos.mapNotNull { info ->
            if (info.activityInfo.packageName != context.packageName) {
                val data =
                    context.packageManager.getApplicationInfo(info.activityInfo.packageName, 0)
                val appInfo = AppInfo().apply {
                    isShow = false
                    name = context.packageManager.getApplicationLabel(data).toString()
                    packName = info.activityInfo.packageName
                    icon = context.packageManager.getApplicationIcon(data)
                }
                appInfo
            } else {
                null
            }
        }.sortedBy { it.name?.uppercase(Locale.getDefault()) }.toMutableList()
    }

    fun getAppListData(): MutableList<AppInfo> {
        return appList
    }

    fun getSavePackName(): MutableList<String>? {
        val data = BaseAppFlash.mmkvFlash.decodeString(BaseAppUtils.app_pack_name, "") ?: ""
        return data.takeIf { it.isNotEmpty() }?.split(",")?.toMutableList()
    }

    fun setSavePackName(appInfo: AppInfo) {
        val savedPackNames = getSavePackName() ?: mutableListOf()
        if (appInfo.isCheck && appInfo.packName !in savedPackNames) {
            BaseAppFlash.mmkvFlash.putString(
                BaseAppUtils.app_pack_name, savedPackNames.plus(appInfo.packName).joinToString(",")
            )
        } else if (!appInfo.isCheck) {
            savedPackNames.remove(appInfo.packName)
            BaseAppFlash.mmkvFlash.putString(
                BaseAppUtils.app_pack_name, savedPackNames.joinToString(",")
            )
        }
    }

    fun setSaveCustom(type: Boolean) {
        BaseAppFlash.mmkvFlash.putBoolean(BaseAppUtils.app_is_custom, type)
    }

    fun pingIPAddress(wang: String): String {
        // 使用 InetAddress 获取 IP 地址
        val ip = try {
            // 解析 URL 并获取主机名
            val urlObj = URL(wang)
            val host = urlObj.host
            InetAddress.getByName(host).hostAddress
        } catch (e: Exception) {
            return "error"
        }

        val command = if (System.getProperty("os.name").startsWith("Windows")) {
            "ping -n 1 $ip"
        } else {
            "ping -c 1 $ip"
        }

        return try {
            val process = ProcessBuilder(*command.split(" ").toTypedArray()).start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.use { it.readText() }
            val pingTime = if (System.getProperty("os.name").startsWith("Windows")) {
                """时间=(\d+)ms""".toRegex().find(output)?.groups?.get(1)?.value
            } else {
                """time=(\d+) ms""".toRegex().find(output)?.groups?.get(1)?.value
            }
            return if (pingTime.isNullOrBlank()) {
                "-"
            } else {
                pingTime
            }
        } catch (e: Exception) {
            "-"
        }
    }

    val pingList = listOf(
        "https://www.tiktok.com/",
        "https://www.netflix.com/",
        "https://www.youtube.com/",
        "https://twitter.com/",
        "https://www.facebook.com/",
        "https://www.instagram.com/",
        "https://store.steampowered.com/",
        "https://store.epicgames.com/",
        "https://www.blizzard.com/"
    )

    @SuppressLint("SetTextI18n")
    fun setTextPing(textView: TextView, pingResult: String) {
        try {
            val num = pingResult.toInt()
            val colorRes = when {
                num <= 150 -> R.color.ping1
                num in 151..250 -> R.color.ping2
                num >= 251 -> R.color.ping3
                else -> R.color.black
            }
            textView.setTextColor(ContextCompat.getColor(textView.context, colorRes))
            textView.text = "$pingResult ms"
        } catch (e: NumberFormatException) {
            textView.text = pingResult
            textView.setTextColor(ContextCompat.getColor(textView.context, R.color.black))
        }
    }


    fun pingAndPrint(activity: PingActivity) {
        activity.lifecycleScope.launch(Dispatchers.IO) {
            val pingResult = pingIPAddress(pingList[0])
            Log.e("TAG", "pingAndPrint=0: ${pingList[0]} : $pingResult ms")
            withContext(Dispatchers.Main) {
                setTextPing(activity.mBinding.tvPingTik, pingResult)
            }
        }
        activity.lifecycleScope.launch(Dispatchers.IO) {
            val pingResult = pingIPAddress(pingList[1])
            Log.e("TAG", "pingAndPrint=1: ${pingList[1]} : $pingResult ms")
            withContext(Dispatchers.Main) {
                setTextPing(activity.mBinding.tvPingNet, pingResult)
            }
        }
        activity.lifecycleScope.launch(Dispatchers.IO) {
            val pingResult = pingIPAddress(pingList[2])
            Log.e("TAG", "pingAndPrint=2: ${pingList[2]} : $pingResult ms")
            withContext(Dispatchers.Main) {
                setTextPing(activity.mBinding.tvPingYou, pingResult)
            }
        }

        activity.lifecycleScope.launch(Dispatchers.IO) {
            val pingResult = pingIPAddress(pingList[3])
            Log.e("TAG", "pingAndPrint=3: ${pingList[3]} : $pingResult ms")
            withContext(Dispatchers.Main) {
                setTextPing(activity.mBinding.tvPingTw, pingResult)
            }
        }
        activity.lifecycleScope.launch(Dispatchers.IO) {
            val pingResult = pingIPAddress(pingList[4])
            Log.e("TAG", "pingAndPrint=4: ${pingList[4]} : $pingResult ms")
            withContext(Dispatchers.Main) {
                setTextPing(activity.mBinding.tvPingFace, pingResult)
            }
        }
        activity.lifecycleScope.launch(Dispatchers.IO) {
            val pingResult = pingIPAddress(pingList[5])
            Log.e("TAG", "pingAndPrint=5: ${pingList[5]} : $pingResult ms")
            withContext(Dispatchers.Main) {
                setTextPing(activity.mBinding.tvPingIns, pingResult)
            }
        }

        activity.lifecycleScope.launch(Dispatchers.IO) {
            val pingResult = pingIPAddress(pingList[6])
            Log.e("TAG", "pingAndPrint=6: ${pingList[6]} : $pingResult ms")
            withContext(Dispatchers.Main) {
                setTextPing(activity.mBinding.tvPingSteam, pingResult)
            }
        }
        activity.lifecycleScope.launch(Dispatchers.IO) {
            val pingResult = pingIPAddress(pingList[7])
            Log.e("TAG", "pingAndPrint=7: ${pingList[7]} : $pingResult ms")
            withContext(Dispatchers.Main) {
                setTextPing(activity.mBinding.tvPingEpic, pingResult)
            }
        }
        activity.lifecycleScope.launch(Dispatchers.IO) {
            val pingResult = pingIPAddress(pingList[8])
            Log.e("TAG", "pingAndPrint=8: ${pingList[8]} : $pingResult ms")
            withContext(Dispatchers.Main) {
                setTextPing(activity.mBinding.tvPingBlizzaed, pingResult)
            }
        }
    }

}