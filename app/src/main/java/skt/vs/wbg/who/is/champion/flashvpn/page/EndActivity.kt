package skt.vs.wbg.who.`is`.champion.flashvpn.page

import android.annotation.SuppressLint
import android.os.Bundle
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseActivityFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.databinding.ConnectedLayoutBinding

class EndActivity : BaseActivityFlash<ConnectedLayoutBinding>() {


    override var conetcntLayoutId: Int
        get() = R.layout.connected_layout
        set(value) {}
    private var isConnected = false


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isConnected = intent.getBooleanExtra("IS_CONNECT", false)
        mBinding.back.setOnClickListener { finish() }
        val data: LocaleProfile
        if (VPNDataHelper.cachePosition != -1) {
            data = VPNDataHelper.allLocaleProfiles[VPNDataHelper.cachePosition]
            if (data.city.isNotBlank()) {
                mBinding.connectCountry.text = data.name + " " + data.city
            } else mBinding.connectCountry.text = data.name
        } else {
            data = VPNDataHelper.allLocaleProfiles[VPNDataHelper.nodeIndex]
            if (data.city.isNotBlank()) {
                mBinding.connectCountry.text = data.name + " " + data.city
            } else mBinding.connectCountry.text = data.name
        }
        mBinding.connectedLocationImage.setImageResource(VPNDataHelper.getImage(data.name))

        when (isConnected) {
            true -> {
                mBinding.tips.text = "Connection succeed"
                mBinding.connectedImage.setImageResource(R.mipmap.connected_ok)
            }

            else -> {
                mBinding.tips.text = "Disconnection succeed"
                mBinding.connectedImage.setImageResource(R.mipmap.connoted_no)
            }
        }
        VPNDataHelper.cachePosition = -1
    }

    override fun onResume() {
        super.onResume()
    }
}