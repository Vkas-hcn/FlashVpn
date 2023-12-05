package skt.vs.wbg.who.`is`.champion.flashvpn.page

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseActivityFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.databinding.ProgressLayoutBinding
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils

class ProgressActivity : BaseActivityFlash<ProgressLayoutBinding>() {

    override var conetcntLayoutId: Int
        get() = R.layout.progress_layout
        set(value) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainScope().launch {
            for (i in 1..100) {
                mBinding.flashProgressBar.progress = i
                delay(20)
            }
            startActivity(Intent(this@ProgressActivity, HomeActivity::class.java))
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        })
    }

}