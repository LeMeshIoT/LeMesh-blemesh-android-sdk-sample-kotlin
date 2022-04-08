# LeMesh-blemesh-android-sdk-sample-kotlin

#### 输入授权信息 

```
// MyApplication
// 输入对应信息,否则初始化的时候会报 "非法授权sdk"
private val appid = "appid"
private val mac = "mac"
private val secret = "secret"
```

#### bleMesh初始化

```
// BleMeshDemoInstance#init 
fun init(context: Context) {
        // todo 自行检查是否开启蓝牙
        val iBleLeMeshManger = LeHomeSdk.getBleLeMeshManger()
        iBleLeMeshManger.initPlugin(
            context
        ) {
            msg.value = "blemesh初始化:${
                if (it == 0) {
                    "完成:${iBleLeMeshManger.deviceMac}"
                } else {
                    "失败:$it"
                }
            }"
        }
    }
```

#### 流程说明

1. 初始化sdk
2. 初始化blemesh模块
3. 添加设备(如果之前添加了可以忽略这步)
4. 控制设备

