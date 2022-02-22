package cn.lelight.iot.blemesh.demo.ui.adddevice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.lelight.iot.blemesh.demo.databinding.ActivityAddDevicesBinding
import cn.lelight.leiot.data.bean.DeviceBean
import cn.lelight.leiot.sdk.LeHomeSdk
import cn.lelight.leiot.sdk.api.IBleLeMeshManger
import cn.lelight.leiot.sdk.api.callback.lemesh.LeMeshAddDeviceCallback

class AddDevicesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddDevicesBinding
    private lateinit var bleLeMeshManger: IBleLeMeshManger

    var addDevices = ArrayList<DeviceBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDevicesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //
        bleLeMeshManger = LeHomeSdk.getBleLeMeshManger()

        bleLeMeshManger.startAddSudDevice(object : LeMeshAddDeviceCallback {
            override fun foundNewSubDevice(p0: DeviceBean?): Boolean {
                // todo 这里可以实现过滤逻辑，如只添加灯具/开关/窗帘
                // todo demo默认所有设备都添加
                return true
            }

            override fun onAddSudDeviceSuccess(p0: DeviceBean?) {
                if (p0 != null) {
                    addDevices.add(p0)
                    //
                    binding.tvAddDeviceMsg.text = "添加设备成功:${addDevices.size}"
                }
            }
        })
    }

    override fun onDestroy() {
        // 停止添加设备
        bleLeMeshManger.stopAddSudDevice()
        super.onDestroy()
    }

}