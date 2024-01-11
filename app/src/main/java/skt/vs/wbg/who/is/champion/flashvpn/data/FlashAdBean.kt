package skt.vs.wbg.who.`is`.champion.flashvpn.data

import androidx.annotation.Keep
@Keep
data class FlashAdBean(
    val onLnugit: String,
    val onLbibl: String,
    val onLconcer: String,
    val onLnose: String,
    val onLmemor: String,
    val bannerId:String,
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

