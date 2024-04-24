package skt.vs.wbg.who.`is`.champion.flashvpn.page

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import skt.vs.wbg.who.`is`.champion.flashvpn.R
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseActivityFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAd
import skt.vs.wbg.who.`is`.champion.flashvpn.base.BaseAppFlash
import skt.vs.wbg.who.`is`.champion.flashvpn.data.AgentViewModel
import skt.vs.wbg.who.`is`.champion.flashvpn.data.AppInfo
import skt.vs.wbg.who.`is`.champion.flashvpn.data.EndViewModel
import skt.vs.wbg.who.`is`.champion.flashvpn.data.MainViewModel
import skt.vs.wbg.who.`is`.champion.flashvpn.databinding.ActivityAgentBinding
import skt.vs.wbg.who.`is`.champion.flashvpn.databinding.ConnectedLayoutBinding
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp
import skt.vs.wbg.who.`is`.champion.flashvpn.tab.DataHelp.putPointYep
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.getLoadBooleanData
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.BaseAppUtils.getLoadStringData
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.ChatUtils
import skt.vs.wbg.who.`is`.champion.flashvpn.utils.GetAppUtils
import java.util.Locale

class AgentActivity : BaseActivityFlash<ActivityAgentBinding>() {
    private lateinit var allApp: MutableList<AppInfo>
    private lateinit var adapter: AgentAdapter
    override var conetcntLayoutId: Int
        get() = R.layout.activity_agent
        set(value) {}
    private val agentViewModel: AgentViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editFun()
        getAppListDataFun {
            initAllAdapter()
        }
        clickFun()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun clickFun() {
        mBinding.showCustom = BaseAppFlash.mmkvFlash.getBoolean(BaseAppUtils.app_is_custom, false)
        mBinding.tvSave.setOnClickListener {
            allApp.forEach {
                GetAppUtils.setSavePackName(it)
            }
            val resultIntent = Intent()
            setResult(Activity.RESULT_OK, resultIntent)
            GetAppUtils.setSaveCustom(mBinding?.showCustom!!)
            finish()
        }
        mBinding.imgBack.setOnClickListener {
            finish()
        }
        mBinding.llGlobal.setOnClickListener {
            mBinding.showCustom = false
        }
        mBinding.llCustom.setOnClickListener {
            mBinding.showCustom = true
        }
        mBinding.llAll.setOnClickListener {
            val allChecked = setIsAll()
            allApp.forEach { it.isCheck = !allChecked }
            adapter.notifyDataSetChanged()
        }
    }

    private fun initAllAdapter() {
        adapter = AgentAdapter(allApp)
        mBinding.rvListApp.adapter = adapter
        mBinding.rvListApp.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        adapter.setOnItemClickListener(object : AgentAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                setSwichApp(allApp[position])
            }
        })
    }

    private fun getAppListDataFun(nextFun: () -> Unit) {
        lifecycleScope.launch(Dispatchers.IO) {
            allApp = GetAppUtils.getAppListData()
            val saveDataList = GetAppUtils.getSavePackName()
            Log.e("TAG", "getAroundFlowAPPList-main: ${saveDataList}")
            if (saveDataList == null) {
                allApp.forEach { pack ->
                    pack.isCheck = false
                }
            }
            saveDataList?.forEach {
                allApp.forEach { pack ->
                    pack.isShow = false
                    if (pack.packName == it) {
                        pack.isCheck = true
                    }
                }
            }
            showIsAll()
            nextFun()
        }
    }

    private fun setIsAll(): Boolean {
        val isChecked = allApp.all { it.isCheck }
        mBinding.imgCheck.setImageResource(if (isChecked) R.drawable.ic_dis_check else R.drawable.ic_check)
        return isChecked
    }

    private fun showIsAll(): Boolean {
        val isChecked = allApp.all { it.isCheck }
        mBinding.imgCheck.setImageResource(if (isChecked) R.drawable.ic_check else R.drawable.ic_dis_check)
        return isChecked
    }


    fun setSwichApp(appInfo: AppInfo) {
        appInfo.isCheck = !appInfo.isCheck
        showIsAll()
        adapter.notifyDataSetChanged()
    }

    private fun editFun() {
        mBinding.etSer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                allApp.forEach { all ->
                    all.isShow = !all.name?.lowercase(Locale.getDefault())?.contains(
                        s.toString()
                            .lowercase(Locale.getDefault())
                    )!!
                }
                adapter.notifyDataSetChanged()
                showNoData()
            }
        })
    }

    fun showNoData() {
        var type = false
        allApp.forEach {
            if (!it.isShow) {
                type = true
            }
        }
        if (type) {
            mBinding.tvNoData.visibility = View.GONE
            mBinding.rvListApp.visibility = View.VISIBLE
        } else {
            mBinding.tvNoData.visibility = View.VISIBLE
            mBinding.rvListApp.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}