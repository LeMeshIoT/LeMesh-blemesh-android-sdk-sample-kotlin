package cn.lelight.iot.blemesh.demo

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.lelight.iot.blemesh.demo.databinding.ActivityCommonDeviceBinding
import cn.lelight.leiot.data.bean.DeviceBean
import cn.lelight.leiot.data.bean.base.DpBean
import cn.lelight.leiot.data.leenum.DeviceType
import cn.lelight.leiot.data.leenum.devsubtype.CommercialDevSubType
import cn.lelight.leiot.data.leenum.dps.*
import cn.lelight.leiot.data.leenum.dps.commercial.CardElectriDp
import cn.lelight.leiot.data.leenum.dps.commercial.TempControllerDp
import cn.lelight.leiot.sdk.LeHomeSdk
import cn.lelight.leiot.sdk.adapter.CommonAdapter
import cn.lelight.leiot.sdk.adapter.ViewHolder
import cn.lelight.leiot.sdk.api.callback.IControlCallback
import cn.lelight.leiot.sdk.api.callback.data.IDevDataListener
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems

class CommonDeviceActivity : AppCompatActivity(), IDevDataListener {

    private var dps: ArrayList<DpPackageBean> = ArrayList()
    private var targetBean: DeviceBean? = null
    private var dpAdapter: DpAdapter? = null

    private lateinit var binding: ActivityCommonDeviceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommonDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //
        //
        val dataManger = LeHomeSdk.getDataManger()
        if (dataManger == null) {
            finish()
            return
        }

        val id = intent.getStringExtra("ID")

        targetBean = dataManger.getDeviceBean(id)
        if (targetBean == null) {
            finish()
            return
        }
        //
        if (supportActionBar != null) {
            supportActionBar!!.title = targetBean!!.mac
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        //
        binding.btnSendHeartbeat.setOnClickListener {
            targetBean?.heartBeat()
        }
        //
        initData()

        LeHomeSdk.getInstance().registerDevDataChangeListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initData() {
        if (targetBean!!.getType() == DeviceType.Light.type) {
            for (value in LightDp.values()) {
                // 添加灯具的功能列表
                val dpPackageBean = DpPackageBean()
                dpPackageBean.id = value.dpId
                dpPackageBean.type = value.type
                dpPackageBean.name = value.getName()
                //
                dps.add(dpPackageBean)
            }
        } else if (targetBean!!.getType() == DeviceType.Curtain.type) {
            for (value in CurtainDp.values()) {
                // 添加灯具的功能列表
                val dpPackageBean = DpPackageBean()
                dpPackageBean.id = value.dpId
                dpPackageBean.type = value.type
                dpPackageBean.name = value.getName()
                //
                dps.add(dpPackageBean)
            }
        } else if (targetBean!!.getType() == DeviceType.Switch.type) {
            for (value in SwitchDp.values()) {
                // 添加灯具的功能列表
                val dpPackageBean = DpPackageBean()
                dpPackageBean.id = value.dpId
                dpPackageBean.type = value.type
                dpPackageBean.name = value.getName()
                //
                dps.add(dpPackageBean)
            }
        } else if (targetBean!!.getType() == DeviceType.Sensor.type) {
            for (value in SensorDp.values()) {
                val dpPackageBean = DpPackageBean()
                dpPackageBean.id = value.dpId
                dpPackageBean.type = value.type
                dpPackageBean.name = value.getName()
                dpPackageBean.mode = value.mode
                //
                dps.add(dpPackageBean)
            }
        } else if (targetBean!!.getType() == DeviceType.Commercial.type) {
            if (targetBean!!.getDevSubType() == CommercialDevSubType.CardElectri.type) {
                for (value in CardElectriDp.values()) {
                    val dpPackageBean = DpPackageBean()
                    dpPackageBean.id = value.dpId
                    dpPackageBean.type = value.type
                    dpPackageBean.name = value.getName()
                    dpPackageBean.mode = value.mode
                    //
                    dps.add(dpPackageBean)
                }
            } else if (targetBean!!.getDevSubType() == CommercialDevSubType.TempController.type) {
                for (value in TempControllerDp.values()) {
                    val dpPackageBean = DpPackageBean()
                    dpPackageBean.id = value.dpId
                    dpPackageBean.type = value.type
                    dpPackageBean.name = value.getName()
                    dpPackageBean.mode = value.mode
                    dpPackageBean.obj = value.desc
                    //
                    dps.add(dpPackageBean)
                }
            }
        }
        //
        for (dp in dps) {
            //==//LelogUtil.e("-----" + dp.toString());
        }
        dpAdapter = DpAdapter(this, dps)
        binding.lvDps.setAdapter(dpAdapter)
    }

    inner class DpAdapter(context: Context?, datas: List<DpPackageBean>) :
        CommonAdapter<DpPackageBean>(context, datas, R.layout.item_common_dp) {
        override fun convert(holder: ViewHolder, dpPackageBean: DpPackageBean) {
            holder.getTextView(R.id.tv_dp_id).text = dpPackageBean.id.toString() + ""
            holder.getTextView(R.id.tv_dp_type).text = dpPackageBean.type.toString() + ""
            for (value in DpType.values()) {
                if (value.type == dpPackageBean.type) {
                    holder.getTextView(R.id.tv_dp_type).text = value.getName()
                    break
                }
            }
            //
            if (targetBean!!.getDps().containsKey(dpPackageBean.id)) {
                holder.getTextView(R.id.tv_dp_value)
                    .setText(targetBean!!.getDps().get(dpPackageBean.id).toString() + "")
            } else {
                holder.getTextView(R.id.tv_dp_value).text = "无"
            }
            //
            //
            if (dpPackageBean.mode == "rw") {
                holder.getTextView(R.id.tv_dp_mode).text = "可下发可上报"
            } else if (dpPackageBean.mode == "ro") {
                holder.getTextView(R.id.tv_dp_mode).text = "仅上报"
            } else if (dpPackageBean.mode == "wr") {
                holder.getTextView(R.id.tv_dp_mode).text = "仅下发"
            }
            //
            holder.getTextView(R.id.tv_dp_name).text = dpPackageBean.name
            //
            holder.getmConverView().setOnClickListener {
                if (dpPackageBean.mode == "ro") {

                } else {
                    if (dpPackageBean.type == DpType.BOOL.type) {
                        showBoolDialog(dpPackageBean)
                    } else if (dpPackageBean.type == DpType.VALUE.type) {
                        showInputValueDialog(dpPackageBean)
                    } else if (dpPackageBean.type == DpType.STR.type) {
                        showInputStrDialog(dpPackageBean)
                    } else if (dpPackageBean.type == DpType.ENUM.type) {
                        if (dpPackageBean.obj is Array<*>) {
                            showSelectEnumDialog(dpPackageBean)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun showInputValueDialog(dpPackageBean: DpPackageBean) {
        //
        MaterialDialog(this)
            .show {
                title(text = dpPackageBean.name!!)
                message(text = "请根据具体范围输入数值\n本弹窗不做范围限制，仅作调试测试用")
                input(
                    hint = "输入数值",
                    allowEmpty = false,
                    inputType = InputType.TYPE_CLASS_NUMBER
                ) { dialog, input ->
                    try {
                        val value = input.toString().toInt()
                        //
                        targetBean!!.sendDp(
                            DpBean(dpPackageBean.id, dpPackageBean.type, value),
                            object : IControlCallback {
                                override fun onSuccess() {
                                    Toast.makeText(
                                        this@CommonDeviceActivity,
                                        "发送成功",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                override fun onFail(code: Int, msg: String) {
                                    Toast.makeText(
                                        this@CommonDeviceActivity,
                                        "发送失败:$msg",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            })
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
    }

    @SuppressLint("CheckResult")
    private fun showSelectEnumDialog(dpPackageBean: DpPackageBean) {
        //
        MaterialDialog(this)
            .show {
                title(text = dpPackageBean.name)
                listItems(items = (dpPackageBean.obj as Array<String>).toList()) { dialog, index, text ->
                    targetBean!!.sendDp(DpBean(
                        dpPackageBean.id,
                        dpPackageBean.type,
                        text.toString()
                    ),
                        object : IControlCallback {
                            override fun onSuccess() {
                                Toast.makeText(
                                    this@CommonDeviceActivity,
                                    "发送成功",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            override fun onFail(code: Int, msg: String) {
                                Toast.makeText(
                                    this@CommonDeviceActivity,
                                    "发送失败:$msg",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                }
            }
    }

    @SuppressLint("CheckResult")
    private fun showInputStrDialog(dpPackageBean: DpPackageBean) {
        MaterialDialog(this)
            .show {
                title(text = dpPackageBean.name!!)
                message(text = "请根据具体范围输入数值\n本弹窗不做范围限制，仅作调试测试用")
                input(
                    hint = "输入内容",
                    allowEmpty = false
                ) { dialog, input ->
                    try {
                        val value = input.toString()
                        // todo
                        targetBean!!.sendDp(
                            DpBean(dpPackageBean.id, dpPackageBean.type, value),
                            object : IControlCallback {
                                override fun onSuccess() {
                                    Toast.makeText(
                                        this@CommonDeviceActivity,
                                        "发送成功",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                override fun onFail(code: Int, msg: String) {
                                    Toast.makeText(
                                        this@CommonDeviceActivity,
                                        "发送失败:$msg",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            })
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
    }

    @SuppressLint("CheckResult")
    private fun showBoolDialog(dpPackageBean: DpPackageBean) {
        val myItems = listOf("打开", "关闭")
        MaterialDialog(this).show {
            title(text = dpPackageBean.name)
            listItems(items = myItems) { dialog, index, text ->
                targetBean!!.sendDp(
                    DpBean(dpPackageBean.id, dpPackageBean.type, index == 0),
                    object : IControlCallback {
                        override fun onSuccess() {}
                        override fun onFail(code: Int, msg: String) {
                            Toast.makeText(
                                this@CommonDeviceActivity,
                                "fail:$msg",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    })
            }
        }
    }

    inner class DpPackageBean {
        var id = 0
        var type = 0
        var mode: String? = null
        var name: String? = null
        var obj: Any? = null

        override fun toString(): String {
            return "DpPackageBean{" +
                    "id=" + id +
                    ", type=" + type +
                    ", name='" + name + '\'' +
                    '}'
        }
    }


    override fun onDeviceAdd(p0: String?) {
    }

    override fun onStatusChanged(p0: String?, p1: Boolean) {
    }

    override fun onDpUpdate(p0: String?, p1: Int, p2: Int, p3: Any?) {
    }

    override fun onDevInfoUpdate(p0: String?) {
        if (dpAdapter != null) {
            dpAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onRemoved(p0: String?) {
    }

    override fun onDestroy() {
        LeHomeSdk.getInstance().unRegisterDevDataChangeListener(this)
        super.onDestroy()
    }
}