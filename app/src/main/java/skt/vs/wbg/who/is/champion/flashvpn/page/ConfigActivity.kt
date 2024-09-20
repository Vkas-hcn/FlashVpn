package skt.vs.wbg.who.`is`.champion.flashvpn.page

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadConnectAd
import skt.vs.wbg.who.`is`.champion.flashvpn.ad.FlashLoadRewardedAd
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseActivityFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAd
import skt.vs.wbg.who.`is`.champion.flashvpn.databinding.ListLayoutBinding
import skt.vs.wbg.who.`is`.champion.flashvpn.page.VPNDataHelper.getImage
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp.putPointFLash
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.FlashOkHttpUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.OnlineVpnHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.ConnectListViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConfigActivity : BaseActivityFlash<ListLayoutBinding>() {
    override var conetcntLayoutId: Int
        get() = R.layout.list_layout
        set(value) {}

    private val listViewModel: ConnectListViewModel by viewModels()
    private var isConnect = false
    var dataList = mutableListOf<LocaleProfile>()
    private lateinit var adapter: LocationsAdapter
    private var stateValue = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getClockState()
        BaseAd.getBackListInstance().advertisementLoadingFlash(this)
        BaseAd.getRewardedInstance().advertisementLoadingFlash(this)
        isConnect = intent.getBooleanExtra("IS_CONNECT", false)
        listViewModel.init(this, isConnect)
        dataList = VPNDataHelper.getAllLocaleProfile()
        val lm = LinearLayoutManager(this)
        adapter = LocationsAdapter(dataList, listViewModel, this)
        mBinding.locationList.layoutManager = lm
        mBinding.locationList.adapter = adapter
        mBinding.back.setOnClickListener {
            listViewModel.showEndScAd(this)
        }
        mBinding.imgRefsh.setOnClickListener {
            refServiceData()
        }
        mBinding.atvRegion.setOnClickListener {

        }

        onBackPressedDispatcher.addCallback(this) {
            listViewModel.showEndScAd(this@ConfigActivity)
        }
    }

    fun getClockState() {
        if (isFirstOpenToday(this)) {
            Log.e("TAG", "getClockState: ")
            SPUtils.getInstance().put(BaseAppUtils.clockIp, "")
        }
    }

    private fun refServiceData() {
        lifecycleScope.launch(Dispatchers.Main) {
            mBinding.inLoad.tvLoading.text = "Update server"
            mBinding.showLoad = true
            FlashOkHttpUtils().getVpnData(this@ConfigActivity) {
                lifecycleScope.launch(Dispatchers.Main) {
                    dataList = VPNDataHelper.getAllLocaleProfile()
                    adapter.setListData(dataList)
                    mBinding.showLoad = false
                    Toast.makeText(this@ConfigActivity, "Refresh successful", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            delay(3000)
            mBinding.showLoad = false
        }
    }

    override fun onResume() {
        super.onResume()
        "o24".putPointFLash(this)
    }
}

class LocationsAdapter(
    private val dataList: MutableList<LocaleProfile>,
    private val listViewModel: ConnectListViewModel,
    private val activity: ConfigActivity
) :
    RecyclerView.Adapter<AdapterViewHolder>() {
    private var showReJob: Job? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_config, parent, false)
        return AdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: AdapterViewHolder, @SuppressLint("RecyclerView") position: Int
    ) {
        val name = holder.itemView.findViewById<AppCompatTextView>(R.id.itemLocationName)
        val image = holder.itemView.findViewById<AppCompatImageView>(R.id.itemLocationImage)
        val check = holder.itemView.findViewById<AppCompatImageView>(R.id.itemLocationCheckImage)
        val imgColck = holder.itemView.findViewById<AppCompatImageView>(R.id.img_colck)
        imgColck.isVisible = dataList[position].isClock
        when (position) {
            0 -> {
                name.text = "Fast Server"
            }

            else -> {
                name.text = dataList[position].name + "-" + dataList[position].city
            }
        }
        Glide.with(holder.itemView.context)
            .load(getImage(dataList[position].name)).into(image)
        if (listViewModel.isConnected && VPNDataHelper.nodeIndex == position) {
            holder.itemView.setBackgroundResource(R.drawable.orange_2)
            check.setImageResource(R.mipmap.flash_checked)
            name.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        } else {
            holder.itemView.setBackgroundResource(R.drawable.gray_12)
            check.setImageResource(R.mipmap.flash_unchecked)
            name.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.tc1))
        }

        holder.itemView.setOnClickListener {
            if (imgColck.isVisible) {
                isSwitchDialog(position)
            } else {
                listViewModel.onItemClick(dataList[position])
            }
        }
    }

    private fun isSwitchDialog(position: Int) {
        val customDialog = Dialog(activity, R.style.AppDialogStyle)
        // Ensure the dialog width is full screen
        customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        customDialog.setContentView(R.layout.dialog_watch)
        customDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val localLayoutParams = customDialog.window?.attributes
        localLayoutParams?.gravity = Gravity.CENTER
        customDialog.window?.attributes = localLayoutParams

        val confirmButton = customDialog.findViewById<LinearLayout>(R.id.watch)
        val cancel = customDialog.findViewById<AppCompatTextView>(R.id.cancelNext)
        confirmButton.setOnClickListener {
            showConfigAd(activity) {
                addStringToLocal(dataList[position].onLm_host)
                Toast.makeText(activity, "The server is unlocked", Toast.LENGTH_SHORT).show()
                notifyDataSetChanged()
                activity.lifecycleScope.launch {
                    delay(200)
                    if (activity.lifecycle.currentState == Lifecycle.State.RESUMED) {
                        listViewModel.onItemClick(dataList[position])
                    }
                }
            }
            customDialog.dismiss()
        }
        cancel.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }


    fun setListData(datas: MutableList<LocaleProfile>) {
        dataList.clear() // 清空当前数据
        dataList.addAll(datas) // 添加排序后的数据
        notifyDataSetChanged() // 更新视图
    }

    private fun showConfigAd(activity: ConfigActivity, nextFun: () -> Unit) {
        showReJob = activity.lifecycleScope.launch {
            val adConnectData = BaseAd.getRewardedInstance().appAdDataFlash
            if (adConnectData == null) {
                BaseAd.getRewardedInstance().advertisementLoadingFlash(activity)
            }
            activity.mBinding.showLoad = true
            delay(1000)
            try {
                withTimeout(12000) {
                    while (isActive) {
                        when (FlashLoadRewardedAd.displayRewardedAdvertisementFlash(
                            activity,
                            closeWindowFun = {
                                Log.e("TAG", "showConfigAd: closeWindowFun")
                                BaseAd.getRewardedInstance().advertisementLoadingFlash(activity)
                                nextFun()
                            })) {
                            2 -> {
                                cancel()
                                showReJob = null
                                activity.mBinding.showLoad = false
                            }
                        }
                        delay(500)
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Toast.makeText(
                    activity,
                    "Ad retrieval timed out, please try again",
                    Toast.LENGTH_SHORT
                ).show()
                showReJob?.cancel()
                showReJob = null
                activity.mBinding.showLoad = false
            }
        }
    }
}

class AdapterViewHolder(view: View) : RecyclerView.ViewHolder(view)


fun saveStringArray(dataList: Array<String>) {
    val dataString = dataList.joinToString(",")
    SPUtils.getInstance().put(BaseAppUtils.clockIp, dataString)
}

fun addStringToLocal(newString: String) {
    val existingData = loadStringArray()
    if (!existingData.contains(newString)) {
        val updatedData = existingData + newString
        saveStringArray(updatedData)
    }
}

fun loadStringArray(): Array<String> {
    val dataString = SPUtils.getInstance().getString(BaseAppUtils.clockIp, "")
    return if (dataString.isEmpty()) {
        arrayOf()
    } else {
        dataString.split(",").toTypedArray()
    }
}

fun isFirstOpenToday(context: Context): Boolean {
    val currentDate = getCurrentDate()
    val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    val savedDate = sharedPreferences.getString("last_open_date", "")

    return if (savedDate == currentDate) {
        false
    } else {
        sharedPreferences.edit().putString("last_open_date", currentDate).apply()
        true
    }
}

fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    return dateFormat.format(Date())
}

