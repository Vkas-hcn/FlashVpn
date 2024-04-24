package skt.vs.wbg.who.`is`.champion.flashvpn.page

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseActivityFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAd
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.data.EndViewModel
import skt.vs.wbg.who.`is`.champion.flashvpn.data.MainViewModel
import skt.vs.wbg.who.`is`.champion.flashvpn.databinding.ConnectedLayoutBinding
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp.putPointYep
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.ChatUtils

class EndActivity : BaseActivityFlash<ConnectedLayoutBinding>() {
    var time: Float = 1f
    private var speedJob: Job? = null
    override var conetcntLayoutId: Int
        get() = R.layout.connected_layout
        set(value) {}
    private var isConnected = false

    private val endViewModel: EndViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isConnected = intent.getBooleanExtra("IS_CONNECT", false)
        mBinding.back.setOnClickListener { endViewModel.showEndScAd(this) }
        ChatUtils.initChart(mBinding.chart)
        val data: LocaleProfile
        if (VPNDataHelper.cachePosition != -1) {
            data = VPNDataHelper.getAllLocaleProfile()[VPNDataHelper.cachePosition]
            if (data.city.isNotBlank()) {
                mBinding.connectCountry.text = data.name + " " + data.city
            } else mBinding.connectCountry.text = data.name
        } else {
            data = VPNDataHelper.getAllLocaleProfile()[VPNDataHelper.nodeIndex]
            if (data.city.isNotBlank()) {
                mBinding.connectCountry.text = data.name + " " + data.city
            } else mBinding.connectCountry.text = data.name
        }
        mBinding.connectedLocationImage.setImageResource(VPNDataHelper.getImage(data.name))
        when (isConnected) {
            true -> {
                getSpeedData()
                mBinding.tips.text = "Connection succeed"
                mBinding.connectedImage.setImageResource(R.mipmap.connected_ok)
            }

            else -> {
                mBinding.tips.text = "Disconnection succeed"
                mBinding.connectedImage.setImageResource(R.mipmap.connoted_no)
            }
        }
        VPNDataHelper.cachePosition = -1
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                endViewModel.showEndScAd(this@EndActivity)
            }
        })
    }

    private fun getSpeedData() {
        speedJob?.cancel()
        speedJob = null
        speedJob = lifecycleScope.launch {
            while (isActive) {
                mBinding.downloadText.text = BaseAppFlash.mmkvFlash.decodeString("speed_dow_online", "0 B")
                mBinding.uploadText.text = BaseAppFlash.mmkvFlash.decodeString("speed_up_online", "0 B")
                time++
                if (DataHelp.isConnectFun()) {
                    ChatUtils.simulateDataUpdate(
                        time,
                        mBinding.chart,
                        mBinding.uploadText.text.toString(),
                        mBinding.downloadText.text.toString()
                    )
                }
                delay(500)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        endViewModel.showEndAd(this)
        "o22".putPointYep(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}