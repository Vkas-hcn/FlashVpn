package skt.vs.wbg.who.`is`.champion.flashvpn.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.google.android.gms.ads.AdActivity
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import skt.vs.wbg.who.`is`.champion.flashvpn.page.ProgressActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.page.SPUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp.putPointYep
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.FlashOkHttpUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.getLoadBooleanData
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.logTagFlash

class BaseAppFlash : Application(), Application.ActivityLifecycleCallbacks {

    companion object {
        var application: BaseAppFlash? = null
        fun getInstance(): BaseAppFlash {
            if (application == null) application = BaseAppFlash()
            return application ?: BaseAppFlash()
        }
        var isHotStart: Boolean = false
        var isUserMainBack: Boolean = false
        var isFlashAppBackGround: Boolean = false
        var acFlashTotal = 0
        var acFlashList = mutableListOf<Activity>()
        var exitAppTime = 0L
        val xkamkaxmak by lazy { MMKV.defaultMMKV() }
        val mmkvFlash by lazy {
            //启用mmkv的多进程功能
            MMKV.mmkvWithID("FlashVpn", MMKV.MULTI_PROCESS_MODE)
        }
        var vpnState = ""
    }

    var adActivity: Activity? = null
    var referJobFlash: Job? = null
    override fun onCreate() {
        super.onCreate()
        application = this
        MMKV.initialize(application)
        BaseAppUtils.initApp(this)
        registerActivityLifecycleCallbacks(this)
        getReferInformation(this)
        "o16".putPointYep(this)

    }


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        acFlashList.add(activity)

    }

    override fun onActivityStarted(activity: Activity) {
        if (activity is AdActivity) {
            adActivity = activity
        }
        acFlashTotal++
        if (isFlashAppBackGround) {
            isFlashAppBackGround = false
            if ((System.currentTimeMillis() - exitAppTime) / 1000 > 3) {
                toSplash(activity)
                if (adActivity != null) adActivity?.finish()
            } else if (isUserMainBack) {
                isUserMainBack = false
                toSplash(activity)
            } else if (activity is ProgressActivity) {
                toSplash(activity)
            }
        }
    }

    private fun toSplash(activity: Activity) {
        "o15".putPointYep(activity)
        val intent = Intent(activity, ProgressActivity::class.java)
        activity.startActivity(intent)
        if (activity is ProgressActivity) activity.finish()
        isHotStart = true
        BaseAd.getHomeInstance().whetherToShowFlash = false
        BaseAd.getEndInstance().whetherToShowFlash = false
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {
        acFlashTotal--
        if (acFlashTotal == 0) {
            isFlashAppBackGround = true
            exitAppTime = System.currentTimeMillis()
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        acFlashList.remove(activity)
    }

    fun getReferInformation(context: Context) {
        referJobFlash?.cancel()
        referJobFlash = GlobalScope.launch {
            while (isActive) {
                if (SPUtils.getInstance().getString(BaseAppUtils.refer_data).isNullOrEmpty()) {
                    getReferrerData(context)
                } else {
                    cancel()
                    referJobFlash = null
                }
                delay(5000)
            }
        }
    }

    private fun getReferrerData(context: Context) {
        var installReferrer =""
        val referrer = SPUtils.getInstance().getString(BaseAppUtils.refer_data)
        if (referrer.isNotBlank()) {
            return
        }
        val date = System.currentTimeMillis()

//        installReferrer = "gclid"
//        installReferrer = "fb4a"
//        SPUtils.getInstance().put(BaseAppUtils.refer_data,installReferrer)

        runCatching {
            val referrerClient = InstallReferrerClient.newBuilder(context).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(p0: Int) {
                    when (p0) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            val installReferrer =
                                referrerClient.installReferrer.installReferrer ?: ""
                            SPUtils.getInstance().put(BaseAppUtils.refer_data,installReferrer)
                            DataHelp.putPointYep("o1unusual",context)
                            val loadDate = (System.currentTimeMillis()-date)/1000
                            DataHelp.putPointTimeYep("o1Obtain",loadDate.toInt(),"conntime",context)
                            if (!BaseAppUtils.refer_tab.getLoadBooleanData()) {
                                runCatching {
                                    referrerClient?.installReferrer?.run {
                                        FlashOkHttpUtils().getInstallList(context, this)
                                    }
                                }.exceptionOrNull()
                            }
                        }
                    }
                    referrerClient.endConnection()
                }

                override fun onInstallReferrerServiceDisconnected() {
                }
            })
        }.onFailure { e ->
            // 处理异常
        }
    }
}