package skt.vs.wbg.who.`is`.champion.flashvpn.data

import android.graphics.drawable.Drawable
import androidx.annotation.Keep
@Keep
data class FlashAdBean(
    val onLnugit: String,
    val onLbibl: String,
    val onLconcer: String,
    val onLnose: String,
    val onLmemor: String,
    val onhhhh:String,
    var loadCity: String,
    var showTheCity: String,

    var loadIp: String,
    var showIp: String,
)
@Keep
data class AdType(
    val id: String,
    val where: String,
    val name:String,
    val type: String,
)
@Keep
data class AppInfo(
    var name: String? = null,
    var icon: Drawable? = null,
    var packName: String? = null,
    var isShow: Boolean = false,
    var isCheck: Boolean = false
)
@Keep
data class DodgingInfo(
    var name: String? = null,
    var parameterName: String? = null,
    var parameterValue: Any? = null,
)

