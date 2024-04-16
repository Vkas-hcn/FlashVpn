package skt.vs.wbg.who.`is`.champion.flashvpn.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
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
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.TAG
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.getLoadBooleanData
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.getLoadStringData
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
        var vpnClickState = -1
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
        initAdJust(this)
    }


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        acFlashList.add(activity)

    }

    override fun onActivityStarted(activity: Activity) {
        acFlashTotal++

        if (activity is AdActivity) {
            adActivity = activity
        }
        if (isFlashAppBackGround) {
            isFlashAppBackGround = false
            if ((System.currentTimeMillis() - exitAppTime) / 1000 > 3) {
                toSplash(activity)
            }
        }

    }

    private fun toSplash(activity: Activity) {
        "o15".putPointYep(activity)
        if (activity is ProgressActivity) {
            activity.finish()
        }
        val intent = Intent(activity, ProgressActivity::class.java)
        activity.startActivity(intent)
        if (adActivity != null) {
            adActivity?.finish()
        }

        isHotStart = true
        BaseAd.getHomeInstance().whetherToShowFlash = false
        BaseAd.getEndInstance().whetherToShowFlash = false
    }

    override fun onActivityResumed(activity: Activity) {
        Adjust.onResume()

    }

    override fun onActivityPaused(activity: Activity) {
        Adjust.onPause()
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
        var installReferrer = ""
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
                            SPUtils.getInstance().put(BaseAppUtils.refer_data, installReferrer)
                            Log.e(TAG, "onInstallReferrerSetupFinished: ${installReferrer}", )
                            val loadDate = (System.currentTimeMillis() - date) / 1000
                            DataHelp.putPointTimeYep(
                                "o1Obtain",
                                loadDate.toInt(),
                                "conntime",
                                context
                            )
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

    @SuppressLint("HardwareIds")
    private fun initAdJust(application: Application) {
        Adjust.addSessionCallbackParameter("customer_user_id", Settings.Secure.getString(application.contentResolver, Settings.Secure.ANDROID_ID))
        val appToken = "ih2pm2dr3k74"
        val environment: String = AdjustConfig.ENVIRONMENT_PRODUCTION
        val config = AdjustConfig(application, appToken, environment)
        config.needsCost = true
        config.setOnAttributionChangedListener { attribution ->
            Log.e("TAG","adjust =${attribution}")
            val data = BaseAppUtils.adjust_data.getLoadStringData()
            if (data.isEmpty() && attribution.network.isNotEmpty() && attribution.network.contains(
                    "organic",
                    true
                ).not()
            ) {
                BaseAppUtils.setLoadData(BaseAppUtils.adjust_data, attribution.network)
            }
        }
        Adjust.onCreate(config)
    }
}