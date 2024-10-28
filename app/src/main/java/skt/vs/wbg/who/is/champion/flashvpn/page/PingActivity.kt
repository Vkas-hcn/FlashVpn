package skt.vs.wbg.who.`is`.champion.flashvpn.page

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseActivityFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.data.AgentViewModel
import skt.vs.wbg.who.`is`.champion.flashvpn.data.AppInfo
import skt.vs.wbg.who.`is`.champion.flashvpn.databinding.ActivityAgentBinding
import skt.vs.wbg.who.`is`.champion.flashvpn.databinding.ActivityPingBinding
import skt.vs.wbg.who.`is`.champion.flashvpn.page.AgentAdapter
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.GetAppUtils

class PingActivity : BaseActivityFlash<ActivityPingBinding>() {
    override var conetcntLayoutId: Int
        get() = R.layout.activity_ping
        set(value) {}

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (mBinding?.showLoading == true) {
                    return
                }
                finish()
            }
        })
        mBinding.inLoad.conDialog.setOnClickListener {  }
        mBinding.imgBack.setOnClickListener {
            finish()
        }
        lifecycleScope.launch(Dispatchers.IO) {
            while (isActive){
                GetAppUtils.pingAndPrint(this@PingActivity)
                delay(2000)
            }
        }
        mBinding.tvPing.setOnClickListener {
            val pingWang = mBinding.etSer.text.toString().trim()
            if (pingWang.isNotBlank()) {
                lifecycleScope.launch(Dispatchers.Main) {
                    mBinding.showLoading = true
                    val ping = withContext(Dispatchers.IO) {
                        GetAppUtils.pingIPAddress(pingWang)
                    }
                    if (ping == "error") {
                        mBinding.showLoading = false
                        Toast.makeText(this@PingActivity, "There is no resolution to the host", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    delay(1000)
                    mBinding.linearLayout.isVisible = true
                    mBinding.tvResult.text = pingWang
                    try {
                        val num = ping.toInt()
                        val colorRes = when {
                            num <= 150 -> R.color.ping1
                            num in 151..250 -> R.color.ping2
                            num >= 251 -> R.color.ping3
                            else -> R.color.black
                        }
                        mBinding.tvPingV.setTextColor(
                            ContextCompat.getColor(
                                mBinding.tvPingV.context,
                                colorRes
                            )
                        )
                        mBinding.tvPingV.text = "$ping ms"
                    } catch (e: NumberFormatException) {
                        mBinding.tvPingV.setTextColor(
                            ContextCompat.getColor(
                                mBinding.tvPingV.context,
                                R.color.black
                            )
                        )
                        mBinding.tvPingV.text = "-"
                    }
                    mBinding.showLoading = false
                }
            } else {
                Toast.makeText(this, "Please enter the URL", Toast.LENGTH_SHORT).show()
            }
        }
    }
}