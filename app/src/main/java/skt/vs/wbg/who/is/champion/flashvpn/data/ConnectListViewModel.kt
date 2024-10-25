package skt.vs.wbg.who.`is`.champion.flashvpn.data

import android.app.Dialog
import android.content.Intent
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadBackListAd
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAd
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.page.ConfigActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.page.EndActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.page.HomeActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.page.LocaleProfile
import skt.vs.wbg.who.`is`.champion.flashvpn.page.VPNDataHelper
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp.putPointFLash
import java.lang.ref.WeakReference

class ConnectListViewModel : ViewModel() {
    private lateinit var activity: WeakReference<ConfigActivity>
    var isConnected: Boolean = false
    private var clickPosition: LocaleProfile? = null
    private var backJob: Job? = null
    fun init(ac: ConfigActivity, isConnected: Boolean) {
        activity = WeakReference(ac)
        this.isConnected = isConnected
    }

    fun onItemClick(localeProfile: LocaleProfile, isChoosePosition: Boolean = false) {
        clickPosition = localeProfile
        if (isChoosePosition && !isConnected) {
            switchVpnConfig()
        } else if (isConnected) {
            activity.get()?.let {
                isSwitchDialog()
            }
        } else {
            switchVpnConfig()
        }
    }

    private fun isSwitchDialog() {
        activity.get()?.let {
            val customDialog = Dialog(it, R.style.AppDialogStyle)
            val localLayoutParams = customDialog.window?.attributes
            localLayoutParams?.gravity = Gravity.CENTER
            customDialog.window?.attributes = localLayoutParams
            customDialog.setContentView(R.layout.dialog_check)
            val confirmButton = customDialog.findViewById<AppCompatTextView>(R.id.confirmmmmmmmmmm)
            val cancel = customDialog.findViewById<AppCompatTextView>(R.id.cancelllllllll)
            confirmButton.setOnClickListener {
                switchVpnConfig(disconnect = true)
                customDialog.dismiss()
            }
            cancel.setOnClickListener {
                customDialog.dismiss()
            }
            customDialog.show()
        }
    }

    private fun switchVpnConfig(
        localeProfileData: LocaleProfile? = clickPosition,
        disconnect: Boolean = false
    ) {
        activity.get().let {
            val list = VPNDataHelper.getAllLocaleProfile()
            var pos = 0
            list.forEachIndexed { index, localeProfile ->
                if (localeProfile.onLm_host == localeProfileData?.onLm_host && localeProfile.name == localeProfileData.name) {
                    pos = index
                }
            }
            if (disconnect) VPNDataHelper.cachePosition = VPNDataHelper.nodeIndex
            VPNDataHelper.nodeIndex = pos
            BaseAppFlash.xkamkaxmak.encode("icConnect", true)
            val intent = Intent(it, HomeActivity::class.java)
            (it as ConfigActivity).setResult(100, intent)
            it.finish()
        }

    }

    fun showEndScAd(activity: ConfigActivity) {
        "o25".putPointFLash(activity)
        if (!DataHelp.isConnectFun()) {
            activity.finish()
            return
        }
        showBackAd(activity) {
            shopJob(activity)
            activity.finish()
        }
    }

    private fun showBackAd(activity: ConfigActivity, nextFun: () -> Unit) {
        backJob?.cancel()
        backJob = null
        backJob = activity.lifecycleScope.launch(Dispatchers.Main) {
            if (FlashLoadBackListAd.canShowAd(activity) == 0) {
                nextFun()
                return@launch
            }
            BaseAd.getBackListInstance().advertisementLoadingFlash(activity)
            val startTime = System.currentTimeMillis()
            var elapsedTime: Long
            activity.mBinding.showLoad = true
            try {
                while (isActive) {
                    elapsedTime = System.currentTimeMillis() - startTime
                    if (isActive && elapsedTime >= 4000L) {
                        nextFun()
                        break
                    }
                    if (elapsedTime >= 1000L && FlashLoadBackListAd.canShowAd(activity) == 2) {
                        shopJob(activity)
                        FlashLoadBackListAd.displayBackAdvertisementFlash(
                            activity,
                            closeWindowFun = {
                                nextFun()
                            })
                    }
                    delay(500L)
                }
            } catch (e: Exception) {
                nextFun()
            }
        }
    }

    private fun shopJob(activity: ConfigActivity) {
        backJob?.cancel()
        backJob = null
        activity.mBinding.showLoad = false
    }
}