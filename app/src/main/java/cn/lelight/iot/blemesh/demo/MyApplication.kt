package cn.lelight.iot.blemesh.demo

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import cn.lelight.leiot.sdk.LeHomeSdk
import cn.lelight.leiot.sdk.bean.InitType
import cn.lelight.leiot.sdk.core.InitCallback

class MyApplication : Application() {

    private val appid = ""
    private val mac = ""
    private val secret = ""

    companion object {
        val isInit = MutableLiveData<Boolean>().apply {
            value = false
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        // 前置初始化
        LeHomeSdk.attachBaseContext(this)
    }

    override fun onCreate() {
        super.onCreate()
        initSdk()
    }

    fun initSdk() {
        LeHomeSdk.init(
            this,
            InitType.NET_ALWAY,
            appid,
            mac,
            secret
        ) { result ->
            Log.e("MainActivity", "sdk init result $result")
            //
            var msg = when (result) {
                InitCallback.SUCCESS -> {
                    BleMeshDemoInstance.get().init(this)

                    val otaManger = LeHomeSdk.getOtaManger()
                    if (otaManger != null) {
                        otaManger.otaCenter.init(applicationContext)
                    }

                    isInit.value = true
                    "sdk 初始化成功"
                }
                InitCallback.ALREADY_INITIALED -> {
                    "sdk已经初始化过了"
                }
                InitCallback.UNAUTH_SDK -> {
                    "非法授权sdk"
                }
                InitCallback.AUTH_FAID -> {
                    // 检查是否设备有网络
                    "授权失败"
                }
                InitCallback.NEED_APPID, InitCallback.NEED_MAC, InitCallback.NEED_SECRET -> {
                    "请填写授权码"
                }
                else -> {
                    "其它异常${result}"
                }
            }
            Toast.makeText(this, msg, Toast.LENGTH_SHORT)
                .show()
        }
    }
}