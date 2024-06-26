package skt.vs.wbg.who.`is`.champion.flashvpn.data

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.VpnService
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.Gravity
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Chronometer
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.blinkt.openvpn.api.ExternalOpenVPNService
import de.blinkt.openvpn.api.IOpenVPNAPIService
import de.blinkt.openvpn.api.IOpenVPNStatusCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash.Companion.isUserMainBack
import skt.vs.wbg.who.`is`.champion.flashvpn.page.ConfigActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.page.EndActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.page.HomeActivity
import skt.vs.wbg.who.`is`.champion.flashvpn.page.LocaleProfile
import skt.vs.wbg.who.`is`.champion.flashvpn.page.VPNDataHelper
import skt.vs.wbg.who.`is`.champion.flashvpn.page.WebFlashActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@SuppressLint("StaticFieldLeak")
class MainViewModel : ViewModel() {
    var userInterrupt = false
    lateinit var activity: WeakReference<HomeActivity>
    var openServerState = MutableLiveData<OpenServiceState>()
    private var shadowsocksJob: Job? = null

    private lateinit var setIcon: AppCompatImageView
    private lateinit var connectImg: AppCompatImageView
    private lateinit var flashListName: AppCompatTextView
    private lateinit var connectAnimate: AppCompatImageView
    private lateinit var flashListIcon: AppCompatImageView
    private lateinit var listCl: ConstraintLayout
    private lateinit var chronometer: Chronometer
    private lateinit var requestPermissionForResultVPN: ActivityResultLauncher<Intent?>
    private lateinit var mainToListResultIntent: ActivityResultLauncher<Intent>
    lateinit var rotateAnimation: Animation
    private var curServerState: String? = ""


    enum class OpenServiceState { CONNECTING, CONNECTED, DISCONNECTING, DISCONNECTED }

    fun init(
        activity: HomeActivity,
        setIcon: AppCompatImageView,
        connectImg: AppCompatImageView,
        connectAnimate: AppCompatImageView,
        listCl: ConstraintLayout,
        flashListIcon: AppCompatImageView,
        flashListName: AppCompatTextView,
        chronometer: Chronometer
    ) {
        this.activity = WeakReference(activity)
        activity.bindService(
            Intent(activity, ExternalOpenVPNService::class.java),
            mConnection,
            AppCompatActivity.BIND_AUTO_CREATE
        )
        this.setIcon = setIcon
        this.connectImg = connectImg
        this.connectAnimate = connectAnimate
        this.listCl = listCl
        this.flashListName = flashListName
        this.flashListIcon = flashListIcon
        this.chronometer = chronometer
        openServerState.postValue(OpenServiceState.DISCONNECTED)
        rotateAnimation = AnimationUtils.loadAnimation(activity, R.anim.ratate_anim)

        initOb()
        initViewAndListener()
        requestPermissionForResultVPN =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                requestPermissionForResult(it)
            }
        mainToListResultIntent =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == 100) {
                    toConnectVerifyNet()
                }
            }

    }


    private fun requestPermissionForResult(result: ActivityResult) {
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            activity.get()?.let { mService?.let { it1 -> toConnectVerifyNet() } }
        } else {
            openServerState.postValue(OpenServiceState.DISCONNECTED)
        }
    }


    private fun initOb() {
        activity.get()?.mBinding?.lifecycleOwner?.let {
            openServerState.observe(it) { state ->
                when (state) {
                    OpenServiceState.CONNECTING -> {
                        setViewEnabled(false)
                    }

                    OpenServiceState.CONNECTED -> {
                        setViewEnabled(true)
                        stopConnectAnimation()
                        setChromometer()
                    }

                    OpenServiceState.DISCONNECTING -> {
                        setViewEnabled(true)
                    }

                    OpenServiceState.DISCONNECTED -> {
                        setViewEnabled(true)
                        stopConnectAnimation()
                        setChromometer()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun stopConnectAnimation() {
        connectAnimate.clearAnimation()
        if (openServerState.value == OpenServiceState.CONNECTED) {
            connectAnimate.setImageResource(R.mipmap.c_okkkkk)
            connectImg.setImageResource(R.mipmap.connect_ok)
        } else {
            connectAnimate.setImageResource(R.mipmap.c_noooo)
            connectImg.setImageResource(R.mipmap.connect_no)
        }
    }

    private fun setViewEnabled(b: Boolean) {
        setIcon.isEnabled = b
        connectAnimate.isEnabled = b
        listCl.isEnabled = b
        connectImg.isEnabled = b
    }

    private fun initViewAndListener() {
        activity.get()?.let { ac ->

            chronometer.onChronometerTickListener = Chronometer.OnChronometerTickListener { cArg ->
                val time = System.currentTimeMillis() - cArg.base
                val d = Date(time)
                val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                chronometer.text = sdf.format(d)
            }

            setIcon.setOnClickListener {
                if (!ac.mBinding.drawer.isOpen) ac.mBinding.drawer.open()
            }
            connectAnimate.setOnClickListener {
                if (ac.isShowGuide) ac.cancelGuideLottie()
                toConnectVerifyNet()
            }
            connectImg.setOnClickListener {
                if (ac.isShowGuide) ac.cancelGuideLottie()
                toConnectVerifyNet()
            }
            listCl.setOnClickListener {
                val intent = Intent(ac, ConfigActivity::class.java)
                intent.putExtra(
                    "IS_CONNECT", openServerState.value == OpenServiceState.CONNECTED
                )

                mainToListResultIntent.launch(intent)
            }
            ac.mBinding.pppppppp.setOnClickListener {
                ac.startActivity(Intent(ac, WebFlashActivity::class.java))
            }

            ac.mBinding.update.setOnClickListener {
                val appPackageName = ac.packageName
                try {
                    val launchIntent = Intent()
                    launchIntent.data = Uri.parse("market://details?id=$appPackageName")
                    ac.startActivity(launchIntent)
                } catch (anfe: ActivityNotFoundException) {
                    ac.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=$appPackageName")
                        )
                    )
                }
            }

            ac.mBinding.clShare.setOnClickListener {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "https://play.google.com/store/apps/details?id=${ac.packageName}"
                )
                sendIntent.type = "text/plain"
                val shareIntent = Intent.createChooser(sendIntent, null)
                ac.startActivity(shareIntent)
            }

            ac.mBinding.lifecycleOwner?.let {
                ac.onBackPressedDispatcher.addCallback(it, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        if (ac.isShowGuide) {
                            ac.cancelGuideLottie()
                        } else if (ac.mBinding.drawer.isOpen) {
                            ac.mBinding.drawer.close()
                        } else if (openServerState.value == OpenServiceState.CONNECTING) {
                        } else if (openServerState.value == OpenServiceState.DISCONNECTING) {
                            stopToConnectOrDisConnect()
                        } else {
                            ac.moveTaskToBack(true)
                            isUserMainBack = true
                        }
                    }
                })
            }
        }

    }

    private fun setChromometer() {
        if (userInterrupt) {
            userInterrupt = false
            if (VPNDataHelper.cachePosition != -1) {
                VPNDataHelper.nodeIndex = VPNDataHelper.cachePosition
                VPNDataHelper.cachePosition = -1
            }
            return
        }
        when (openServerState.value) {
            OpenServiceState.CONNECTED -> {
                chronometer.base = System.currentTimeMillis()
                chronometer.start()
            }

            OpenServiceState.DISCONNECTED -> {
                chronometer.stop()
                chronometer.base = SystemClock.elapsedRealtime()
            }

            else -> {}
        }
    }


    private fun toEndAc() {
        activity.get()?.let {
            val intent = Intent(it, EndActivity::class.java)
            intent.putExtra(
                "IS_CONNECT", curServerState == "CONNECTED"
            )
            it.startActivity(intent)
        }
    }

    fun activityResume() {
        if (VPNDataHelper.cachePosition != -1) {
            val node: LocaleProfile = VPNDataHelper.allLocaleProfiles[VPNDataHelper.cachePosition]
            flashListIcon.setImageResource(VPNDataHelper.getImage(node.name))
            flashListName.text =
                if (node.city.isNotBlank()) node.name + "-" + node.city else node.name
        } else {
            val node: LocaleProfile = VPNDataHelper.allLocaleProfiles[VPNDataHelper.nodeIndex]
            flashListIcon.setImageResource(VPNDataHelper.getImage(node.name))
            flashListName.text =
                if (node.city.isNotBlank()) node.name + "-" + node.city else node.name
        }
    }

    fun stopToConnectOrDisConnect() {
        if (shadowsocksJob?.isActive == true) {
            shadowsocksJob?.cancel()
            when (openServerState.value) {
                OpenServiceState.CONNECTING -> {
                    openServerState.postValue(OpenServiceState.DISCONNECTED)
                    mService?.disconnect()
                    cancelConnect = true
//                    Log.e("open vpn", "Connect cancel")
                }

                OpenServiceState.DISCONNECTING -> {
                    userInterrupt = true
                    openServerState.postValue(OpenServiceState.CONNECTED)
                }

                else -> {}
            }
        }
    }

    fun toConnectVerifyNet() {
        if (isAppOnline(activity.get())) {
            toConnectOrDisConnect()
        } else {
            activity.get()?.let {
                val customDialog = Dialog(it, R.style.AppDialogStyle)
                val localLayoutParams = customDialog.window?.attributes
                localLayoutParams?.gravity = Gravity.CENTER
                customDialog.window?.attributes = localLayoutParams
                customDialog.setContentView(R.layout.nettttttttttttttttt)
                val confirmButton = customDialog.findViewById<AppCompatTextView>(R.id.confirmButton)
                confirmButton.setOnClickListener {
                    customDialog.dismiss()
                }
                customDialog.show()
            }
        }
    }

    private var toAction = false

    private fun toConnectOrDisConnect() {
        activity.get()?.let {
            toAction = true
            cancelConnect = false
            when (openServerState.value) {
                OpenServiceState.CONNECTED -> {
                    playConnectAnimation()
                    shadowsocksJob = disconnectShadowsocks()
                }

                OpenServiceState.DISCONNECTED -> {
                    playConnectAnimation()
                    shadowsocksJob =
                        activity.get()?.let { mService?.let { it1 -> openVTool(it, it1) } }
                }

                else -> {}
            }
        }
    }

    private fun playConnectAnimation() {
        connectAnimate.setImageResource(R.mipmap.c_loading)
        connectAnimate.startAnimation(rotateAnimation)
    }

    private fun disconnectShadowsocks(): Job {
        val job = MainScope().launch(Dispatchers.IO) {
            activity.get().let { ac ->
                if (ac != null) {
                    openServerState.postValue(OpenServiceState.DISCONNECTING)
                    delay(2000)
                }
                if (ac?.mBinding?.drawer?.isOpen == true) {
                    userInterrupt = true
                    stopToConnectOrDisConnect()
                    return@launch
                } else if (ac?.canJump == false) {
                    return@launch
                } else if (isActive) {
                    mService?.disconnect()
                }
            }
        }
        return job
    }


    private fun isAppOnline(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        if (networkCapabilities != null) {
            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }
        return false
    }


    private fun checkVPNPermission(ac: Activity): Boolean {
        VpnService.prepare(ac).let {
            return it == null
        }
    }


    var mService: IOpenVPNAPIService? = null

    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            className: ComponentName?,
            service: IBinder?,
        ) {
            mService = IOpenVPNAPIService.Stub.asInterface(service)
            try {
                mService?.registerStatusCallback(mCallback)
//                Log.e("open vpn mService ", "mService onServiceConnected")
            } catch (e: Exception) {
//                Log.e("open vpn error", e.message.toString())
            }
        }

        override fun onServiceDisconnected(className: ComponentName?) {
//            Log.e("open vpn mService ", "mService onServiceDisconnected")
            mService = null
        }
    }

    private val mCallback = object : IOpenVPNStatusCallback.Stub() {
        override fun newStatus(uuid: String?, state: String?, message: String?, level: String?) {
            // NOPROCESS 未连接 // CONNECTED 已连接
            // RECONNECTING 尝试重新链接 // EXITING 连接中主动掉用断开
            Log.e(
                "open vpn state",
                "${uuid.toString()},${state.toString()},${message.toString()},${level.toString()}"
            )
            curServerState = state
            when (state) {
                "CONNECTED" -> {
                    if (toAction) toAction = false
                    userInterrupt = false
                    openServerState.postValue(OpenServiceState.CONNECTED)
                    toEndAc()

                }

                "RECONNECTING" -> {
                    Toast.makeText(activity.get(), "Reconnecting", Toast.LENGTH_LONG).show()
                }

                "NOPROCESS" -> {
                    if (toAction) {
                        toAction = false
                        userInterrupt = false
                        openServerState.postValue(OpenServiceState.DISCONNECTED)
                        if (!cancelConnect) toEndAc()
                        else cancelConnect = false
                    }
                }


                else -> {}
            }

        }

    }
    var cancelConnect = false

    fun openVTool(context: Context, server: IOpenVPNAPIService): Job? {
        activity.get()?.let { ac ->
            if (checkVPNPermission(ac)) {
                val job = MainScope().launch(Dispatchers.IO) {
                    openServerState.postValue(OpenServiceState.CONNECTING)
                    if (isAppOnline(ac)) {
                        val data = VPNDataHelper.allLocaleProfiles[VPNDataHelper.nodeIndex]
                        runCatching {
                            val conf = context.assets.open("fast_onlinenetmanager.ovpn")
                            val br = BufferedReader(InputStreamReader(conf))
                            val config = StringBuilder()
                            var line: String?
                            while (true) {
                                line = br.readLine()
                                if (line == null) break
                                if (line.contains("remote 66", true)) {
                                    line = "remote ${data.onLm_host} ${data.onLo_Port}"
//                                    Log.e("open vpn remote", line.toString())
                                } else if (line.contains("wrongpassword", true)) {
                                    line = data.onLu_password
//                                    Log.e("open vpn pass", line.toString())
                                } else if (line.contains("cipher AES-256-GCM", true)) {
                                    line = "cipher ${data.onLi}"
//                                    Log.e("open vpn pass", line.toString())
                                }
                                config.append(line).append("\n")
                            }
                            br.close()
                            conf.close()
                            server.startVPN(config.toString())
                            delay(12000)
                            if (curServerState != "CONNECTED" && ac.canJump) {
                                cancelConnect = true
                                stopToConnectOrDisConnect()
                                Looper.prepare()
                                Toast.makeText(activity.get(), "Connect Failed!", Toast.LENGTH_LONG)
                                    .show()
                                Looper.loop()
                                cancel()
                            }
                        }.onFailure {
//                            Log.e("open vpn error", it.message.toString())
                        }
                    } else {
                        stopToConnectOrDisConnect()
                    }

                }
                return job

            } else {
                VpnService.prepare(ac).let {
                    requestPermissionForResultVPN.launch(it)
                }
            }
        }
        return null
    }


}