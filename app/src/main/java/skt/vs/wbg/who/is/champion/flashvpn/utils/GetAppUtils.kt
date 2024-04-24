package skt.vs.wbg.who.`is`.champion.flashvpn.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.data.AppInfo
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.getLoadBooleanData
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.getLoadStringData
import java.util.Locale

object GetAppUtils {
    private var appList: MutableList<AppInfo> = mutableListOf()

    fun getAllLauncherIconPackages(context: Context) {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val resolveInfos =
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        appList = resolveInfos.mapNotNull { info ->
            if (info.activityInfo.packageName != context.packageName) {
                val data =
                    context.packageManager.getApplicationInfo(info.activityInfo.packageName, 0)
                val appInfo = AppInfo().apply {
                    isShow = false
                    name = context.packageManager.getApplicationLabel(data).toString()
                    packName = info.activityInfo.packageName
                    icon = context.packageManager.getApplicationIcon(data)
                }
                appInfo
            } else {
                null
            }
        }.sortedBy { it.name?.uppercase(Locale.getDefault()) }.toMutableList()
    }

    fun getAppListData(): MutableList<AppInfo> {
        return appList
    }

    fun getSavePackName(): MutableList<String>? {
        val data = BaseAppFlash.mmkvFlash.decodeString(BaseAppUtils.app_pack_name, "") ?: ""
        return data.takeIf { it.isNotEmpty() }?.split(",")?.toMutableList()
    }

    fun setSavePackName(appInfo: AppInfo) {
        val savedPackNames = getSavePackName() ?: mutableListOf()
        if (appInfo.isCheck && appInfo.packName !in savedPackNames) {
            BaseAppFlash.mmkvFlash.putString(
                BaseAppUtils.app_pack_name,
                savedPackNames.plus(appInfo.packName).joinToString(",")
            )
        } else if (!appInfo.isCheck) {
            savedPackNames.remove(appInfo.packName)
            BaseAppFlash.mmkvFlash.putString(
                BaseAppUtils.app_pack_name,
                savedPackNames.joinToString(",")
            )
        }
    }

    fun setSaveCustom(type: Boolean) {
        BaseAppFlash.mmkvFlash.putBoolean(BaseAppUtils.app_is_custom, type)
    }
}