package cn.lelight.iot.blemesh.demo

import android.content.Context
import androidx.lifecycle.MutableLiveData
import cn.lelight.leiot.sdk.LeHomeSdk
import cn.lelight.leiot.sdk.utils.ShareUtils

class BleMeshDemoInstance private constructor() {

    companion object {
        private var instance: BleMeshDemoInstance? = null
            get() {
                if (field == null) {
                    field = BleMeshDemoInstance()
                }
                return field
            }

        fun get(): BleMeshDemoInstance {
            return instance!!
        }
    }

    val reSendTime = MutableLiveData<Int>().apply {
        if (!ShareUtils.getInstance().contain("reSendTime")) {
            this.value = 3
        } else {
            this.value = ShareUtils.getInstance().getInt("reSendTime")
        }
    }

    val isEachControl = MutableLiveData<Boolean>().apply {
        if (!ShareUtils.getInstance().contain("isEachControl")) {
            this.value = false
        } else {
            this.value = ShareUtils.getInstance().getBoolean("isEachControl")
        }
    }

    val subDevTime = MutableLiveData<Int>().apply {
        if (!ShareUtils.getInstance().contain("subDevTime")) {
            this.value = 120
        } else {
            this.value = ShareUtils.getInstance().getInt("subDevTime")
        }
    }

    //
    val reScanTime = MutableLiveData<Int>().apply {
        if (!ShareUtils.getInstance().contain("reScanTime")) {
            this.value = 120
        } else {
            this.value = ShareUtils.getInstance().getInt("reScanTime")
        }
    }

    var msg = MutableLiveData<String>().apply {
        value = "未初始化"
    }

    fun init(context: Context) {
        // todo 自行检查是否开启蓝牙
        val iBleLeMeshManger = LeHomeSdk.getBleLeMeshManger()
        iBleLeMeshManger.initPlugin(
            context
        ) {
            msg.postValue(
                "blemesh初始化:${
                    if (it == 0) {
                        "完成:${iBleLeMeshManger.deviceMac}"
                    } else {
                        "失败:$it"
                    }
                }"
            )
        }
        //
        iBleLeMeshManger.reSendTime = reSendTime.value!!
        iBleLeMeshManger.setGroupControlEachAgain(isEachControl.value!!)
        iBleLeMeshManger.setSubDevHeartbeatTime(subDevTime.value!!)
        iBleLeMeshManger.setCheckScanSubDevTime(reScanTime.value!!)
    }

}