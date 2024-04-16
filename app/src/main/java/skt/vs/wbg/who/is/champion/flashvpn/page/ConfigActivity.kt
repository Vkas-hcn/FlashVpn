package skt.vs.wbg.who.`is`.champion.flashvpn.page

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseActivityFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAd
import skt.vs.wbg.who.`is`.champion.flashvpn.databinding.ListLayoutBinding
import skt.vs.wbg.who.`is`.champion.flashvpn.page.VPNDataHelper.getImage
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp.putPointYep
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.ConnectListViewModel

class ConfigActivity : BaseActivityFlash<ListLayoutBinding>() {
    override var conetcntLayoutId: Int
        get() = R.layout.list_layout
        set(value) {}

    private val listViewModel: ConnectListViewModel by viewModels()
    private var isConnect = false
    var dataList = mutableListOf<LocaleProfile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BaseAd.getBackInstance().advertisementLoadingFlash(this)
        isConnect = intent.getBooleanExtra("IS_CONNECT", false)
        listViewModel.init(this, isConnect)
        dataList = VPNDataHelper.allLocaleProfiles
        val lm = LinearLayoutManager(this)
        val adapter = LocationsAdapter(dataList, listViewModel)
        mBinding.locationList.layoutManager = lm
        mBinding.locationList.adapter = adapter
        mBinding.back.setOnClickListener {
            listViewModel.showEndScAd(this)
        }
        onBackPressedDispatcher.addCallback(this){
            listViewModel.showEndScAd(this@ConfigActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        "o24".putPointYep(this)
    }
}

class LocationsAdapter(
    private val dataList: MutableList<LocaleProfile>,
    private val listViewModel: ConnectListViewModel
) :
    RecyclerView.Adapter<AdapterViewHolder>() {
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
        if (position == 0) {
            name.text = "Fast Server"
            image.setImageResource(R.mipmap.flash_list_icon)
        } else {
            name.text = dataList[position].name + "-" + dataList[position].city
            Glide.with(holder.itemView.context)
                .load(getImage(dataList[position].name)).into(image)
        }
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
            listViewModel.onItemClick(position)
        }
    }


}

class AdapterViewHolder(view: View) : RecyclerView.ViewHolder(view)