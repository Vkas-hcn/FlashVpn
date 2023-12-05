package skt.vs.wbg.who.`is`.champion.flashvpn.utils

import android.app.ActivityManager
import android.app.Application
import android.os.Build
import android.os.Process
import android.webkit.WebView
import com.tencent.mmkv.MMKV
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.net.FlashCloak
import skt.vs.wbg.who.`is`.champion.flashvpn.page.SPUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.page.VPNDataHelper.initVpnFb
import java.text.SimpleDateFormat
import java.util.Date

object BaseAppUtils {
    const val logTagFlash = "FlashVPN"
    fun initApp(application: Application) {
        val myPid = Process.myPid()
        val activityManager =
            application.getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager
        val processInfoList = activityManager.runningAppProcesses
        val packageName = application.packageName
        for (info in processInfoList) {
            if (info!!.pid == myPid && packageName == info.processName) {
//              Firebase.initialize(this)
                MMKV.initialize(application)
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
    fun getJson(fileName: String): String {
        val stringBuilder = StringBuilder()
        try {
            val assetManager = BaseAppFlash.xkamkaxmak.assets
            val bf = assetManager.open(fileName)
            val bufferedReader = bf.bufferedReader()
            bufferedReader.use {
                it.readLines().forEach { line ->
                    stringBuilder.append(line)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }
}