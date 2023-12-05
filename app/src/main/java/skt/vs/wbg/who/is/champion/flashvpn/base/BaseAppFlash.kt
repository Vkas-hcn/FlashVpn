package skt.vs.wbg.who.`is`.champion.flashvpn.base

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.tencent.mmkv.MMKV
import skt.vs.wbg.who.`is`.champion.flashvpn.page.ProgressActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils

class BaseAppFlash : Application(), Application.ActivityLifecycleCallbacks {

    companion object {
        var application: BaseAppFlash? = null
        fun getInstance(): BaseAppFlash {
            if (application == null) application = BaseAppFlash()
            return application ?: BaseAppFlash()
        }

        var isUserMainBack: Boolean = false
        var isFlashAppBackGround: Boolean = false
        var acFlashTotal = 0
        var acFlashList = mutableListOf<Activity>()
        var exitAppTime = 0L
        val xkamkaxmak by lazy { MMKV.defaultMMKV() }

    }

    override fun onCreate() {
        super.onCreate()
        application = this
        BaseAppUtils.initApp(this)
        registerActivityLifecycleCallbacks(this)

    }


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        acFlashList.add(activity)

    }

    override fun onActivityStarted(activity: Activity) {
        acFlashTotal++
        if (isFlashAppBackGround) {
            isFlashAppBackGround = false
            if ((System.currentTimeMillis() - exitAppTime) / 1000 > 3) {
                toSplash(activity)
            } else if (isUserMainBack) {
                isUserMainBack = false
                toSplash(activity)
            } else if (activity is ProgressActivity) {
                toSplash(activity)
            }
        }
    }

    private fun toSplash(activity: Activity) {
        val intent = Intent(activity, ProgressActivity::class.java)
        activity.startActivity(intent)
        if (activity is ProgressActivity) activity.finish()
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
}