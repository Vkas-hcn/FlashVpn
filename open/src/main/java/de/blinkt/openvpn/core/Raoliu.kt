package de.blinkt.openvpn.core

import android.content.Context
import android.net.VpnService
import android.util.Log
import com.tencent.mmkv.MMKV

object Raoliu {
    private val mmkv by lazy {
        MMKV.mmkvWithID("FlashVpn", MMKV.MULTI_PROCESS_MODE)
    }

    private fun getFlowAppList(): List<String>? {
        val data = mmkv.decodeString("a_p_n", "") ?: return null
        if (data.isEmpty()) {
            return null
        }
        return data.split(",").toMutableList()
    }

    private fun getFlowData(): Boolean {
        val data = mmkv.decodeBool("raoliu", true)
        Log.e("TAG", "getAroundFlowJsonData: ${data}")
        return data
    }
    private fun getFlowCustomAll(): Boolean {
        val data = mmkv.decodeBool("app_is_custom", true)
        Log.e("TAG", "getAroundFlowCustomData: ${data}")
        return data
    }

    fun brand(builder: VpnService.Builder, myPackageName: String) {
        if (getFlowData()) {
            //黑名单绕流
            (listOf(myPackageName) + listGmsPackages())
                .iterator()
                .forEachRemaining {
                    runCatching { builder.addDisallowedApplication(it) }
                }
        }

        if(getFlowCustomAll()){
            val dataList = getFlowAppList()
            Log.e("TAG", "getAroundFlowAPPList: ${dataList}")
            (dataList)?.iterator()?.forEachRemaining {
                runCatching { builder.addDisallowedApplication(it) }
            }
        }
    }

    private fun listGmsPackages(): List<String> {
        return listOf(
            "com.google.android.gms",
            "com.google.android.ext.services",
            "com.google.process.gservices",
            "com.android.vending",
            "com.google.android.gms.persistent",
            "com.google.android.cellbroadcastservice",
            "com.google.android.packageinstaller",
            "com.google.android.gms.location.history",
        )
    }

    fun getSpeedData(upData: String, downData: String) {
        mmkv.encode("speed_dow_online", downData)
        mmkv.encode("speed_up_online", upData)
    }
}