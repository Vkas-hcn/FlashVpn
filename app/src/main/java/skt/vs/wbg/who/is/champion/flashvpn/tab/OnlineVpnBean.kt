package skt.vs.wbg.who.`is`.champion.flashvpn.tab

import androidx.annotation.Keep

@Keep
data class OnlineVpnBean(
    val code: Int,
    val `data`: Data,
    val msg: String
)
@Keep
data class Data(
    val sXs: List<SX>,
    val szzCkEVIjK: List<SzzCkEVIjK>
)
@Keep
data class SX(
    val BDwHY: String,
    val DHaQGCA: List<String>,
    val ErIHMhQH: String,
    val JEt: String,
    val TlUh: String,
    val UiqMt: String,
    val VINC: String,
    val XMQHeeqrK: Int,
    val euRFK: String,
    val wPKfK: String
)
@Keep
data class SzzCkEVIjK(
    val BDwHY: String,
    val DHaQGCA: List<Any>,
    val ErIHMhQH: String,
    val JEt: String,
    val TlUh: String,
    val UiqMt: String,
    val VINC: String,
    val XMQHeeqrK: Int,
    val euRFK: String,
    val wPKfK: String
)