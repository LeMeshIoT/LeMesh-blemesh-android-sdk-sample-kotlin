package cn.lelight.iot.blemesh.demo.ui.devices

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import cn.lelight.iot.blemesh.demo.CommonDeviceActivity
import cn.lelight.iot.blemesh.demo.R
import cn.lelight.leiot.data.bean.DeviceBean
import cn.lelight.leiot.data.leenum.DeviceType
import cn.lelight.leiot.sdk.LeHomeSdk
import cn.lelight.leiot.sdk.adapter.CommonAdapter
import cn.lelight.leiot.sdk.adapter.ViewHolder
import cn.lelight.leiot.sdk.api.callback.IDeleteDeviceCallback
import cn.lelight.leiot.sdk.utils.Arrays
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import kotlin.experimental.and

internal class DevicesAdapter(context: Context?, datas: List<DeviceBean?>?) :
    CommonAdapter<DeviceBean>(context, datas, R.layout.item_data_device) {
    override fun convert(holder: ViewHolder, deviceBean: DeviceBean) {

        holder.getImageView(R.id.iv_icon).setImageResource(
            when (deviceBean.type) {
                DeviceType.Light.type ->
                    R.drawable.ic_sim_ble
                DeviceType.Curtain.type ->
                    R.drawable.public_icon_blind_a
                DeviceType.Switch.type ->
                    R.drawable.ic_tough_switch_icon_one_on
                else ->
                    R.drawable.public_icon_unkown
            }

        )

        holder.getTextView(R.id.tv_device_rename).setOnClickListener {
            MaterialDialog(mContext).show {
                title(text = "请输入名字")
                input(hint = "", allowEmpty = false) { dialog, input ->
                    var name = input.toString()
                    deviceBean.reName(name)
                }
            }
        }

        holder.getTextView(R.id.tv_device_name).text = deviceBean.getName()
        holder.getTextView(R.id.tv_device_mac).text =
            "MAC:" + deviceBean.getMac() + " " + "0x" + String.format(
                "%02x",
                deviceBean.getDevSubType()
            ).toUpperCase()

        holder.getTextView(R.id.tv_device_kind).text = if (deviceBean.isOnline) {
            "在线"
        } else {
            "离线"
        }
        //
        holder.getmConverView().setOnLongClickListener {
            // 点击事件

            //
            MaterialDialog(mContext).show {
                title(text = "输入命令和参数(16进制)")
                message(text = "不清楚请勿随意设置")
                input { dialog, input ->
                    val hexToBytes = Arrays.hexToBytes(input.toString())

                    LeHomeSdk.getBleLeMeshManger().sendGwMsg(
                        deviceBean.getMac(),
                        deviceBean.getPid().toInt(16),
                        1,
                        (hexToBytes[0].and(0xFF.toByte())).toInt(),
                        input.toString().substring(2)
                    )
                }
                positiveButton(text = "发送")
                negativeButton(text = "取消")
            }

            return@setOnLongClickListener true
        }
        //
        holder.getmConverView().setOnClickListener {
            // 点击事件
            mContext.startActivity(Intent(mContext, CommonDeviceActivity::class.java).apply {
                putExtra("ID", deviceBean.devId)
            })
        }
        //
        holder.getView<View>(R.id.tv_device_del).setOnClickListener { //
            MaterialDialog(mContext).show {
                title(text = "确定删除设备?")
                message(text = "请保持设备通电在线,否则需要8短重置才可重新添加")
                positiveButton(text = "开始删除") {
                    deviceBean.onDelete(object : IDeleteDeviceCallback {
                        override fun onDeleteSuccess() {
                            Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show()
                        }

                        override fun onDeleteFail(msg: String) {
                            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show()
                            //
                            showDeleteData(msg, deviceBean)
                        }
                    })
                }
                negativeButton(text = "取消")
            }
        }
    }

    private fun showDeleteData(msg: String, deviceBean: DeviceBean) {
        MaterialDialog(mContext).show {
            title(text = "删除失败")
            message(
                text = "原因:$msg\n" +
                        "是否强制删除本地数据?\n" +
                        "(设备需要8短重置)"
            )
            positiveButton(text = "开始删除") {
                deviceBean.onDeleteData()
            }
            negativeButton(text = "取消")
        }
    }
}