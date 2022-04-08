package cn.lelight.iot.blemesh.demo

import android.content.Context
import androidx.lifecycle.MutableLiveData
import cn.lelight.leiot.sdk.LeHomeSdk

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
        }.setGroupControlEachAgain(true)
    }

}