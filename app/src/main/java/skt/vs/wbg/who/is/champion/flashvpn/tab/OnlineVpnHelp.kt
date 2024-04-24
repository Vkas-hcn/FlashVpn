package skt.vs.wbg.who.`is`.champion.flashvpn.tab

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import skt.vs.wbg.who.`is`.champion.flashvpn.page.LocaleProfile
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.TAG
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.getLoadStringData

object OnlineVpnHelp {
    /**
     * 检查是否有下发服务器数据
     */
    fun checkServerData(context: Context): Boolean {
        val data = getDataFromTheServer()
        return if (data == null) {
            FlashOkHttpUtils().getVpnData(context){}
            false
        } else {
            true
        }
    }
    /**
     * 获取下发服务器数据
     */
     fun getDataFromTheServer(): MutableList<LocaleProfile>? {
        val data = BaseAppUtils.vpn_online.getLoadStringData()
        return runCatching {
            val spinVpnBean = Gson().fromJson(data, OnlineVpnBean::class.java)
            val data = spinVpnBean.data.sXs
            val data2 = data.distinctBy { it.wPKfK }
            Log.e(TAG,"data=${data}")
            if (data2.isNotEmpty()) {
                data2.map {
                    LocaleProfile().apply {
                        onLm_host = it.wPKfK
                        onLo_Port = it.XMQHeeqrK
                        onLi = it.UiqMt
                        onLu_password = it.VINC
                        city = it.JEt
                        name = it.BDwHY
                    }
                }.toMutableList()
            } else {
                null
            }
        }.getOrElse {
            null
        }
    }

    fun getDataFastServerData(): MutableList<LocaleProfile>? {
        val data =  BaseAppUtils.vpn_online.getLoadStringData()
        return runCatching {
            val spinVpnBean = Gson().fromJson(data, OnlineVpnBean::class.java)
            val data = spinVpnBean.data.szzCkEVIjK
            if (data.isNotEmpty()) {
                data.distinctBy { it.wPKfK }.map {
                    LocaleProfile().apply {
                        onLm_host = it.wPKfK
                        onLo_Port = it.XMQHeeqrK
                        onLi = it.UiqMt
                        onLu_password = it.VINC
                        city = it.JEt
                        name = it.BDwHY
                    }
                }.toMutableList()
            } else {
                null
            }
        }.getOrElse {
            null
        }
    }
}