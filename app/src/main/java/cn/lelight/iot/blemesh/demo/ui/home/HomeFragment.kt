package cn.lelight.iot.blemesh.demo.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cn.lelight.iot.blemesh.demo.BleMeshDemoInstance
import cn.lelight.iot.blemesh.demo.BuildConfig
import cn.lelight.iot.blemesh.demo.databinding.FragmentHomeBinding
import cn.lelight.iot.blemesh.demo.ui.adddevice.AddDevicesActivity
import cn.lelight.leiot.data.bean.DeviceBean
import cn.lelight.leiot.data.ota.OtaFileBean
import cn.lelight.leiot.sdk.LeHomeSdk
import cn.lelight.leiot.sdk.api.callback.ota.OtaCheckListener
import cn.lelight.leiot.sdk.api.callback.ota.OtaListener
import com.afollestad.materialdialogs.MaterialDialog


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    lateinit var homeViewModel: HomeViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var materialDialog: MaterialDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        BleMeshDemoInstance.get().msg.observe(viewLifecycleOwner) {
            binding.textHome2.text = it
            materialDialog?.dismiss()
        }

        if (LeHomeSdk.getBleLeMeshManger() != null) {
            binding.textHome.text =
                "ver:${LeHomeSdk.getBleLeMeshManger().version}\n${BuildConfig.BUILDTIME}"
        }

        binding.btnSetHeartbeat.setOnClickListener {
            if (LeHomeSdk.getBleLeMeshManger() == null) {
                Toast.makeText(requireContext(), "未初始化/依赖错误", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            MaterialDialog.Builder(requireActivity())
                .title("设置子设备心跳")
                .content("默认是120s,建议初始化时候设置")
                .input("", "", false) { dialog, input ->
                    var numer = input.toString().toInt()
                    LeHomeSdk.getBleLeMeshManger().setSubDevHeartbeatTime(numer)
                }
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .show()
        }

        binding.btnAddDevice.setOnClickListener {
            // 添加设备
            if (LeHomeSdk.getBleLeMeshManger() == null) {
                Toast.makeText(requireContext(), "未初始化/依赖错误", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            requireActivity().startActivity(
                Intent(
                    requireActivity(),
                    AddDevicesActivity::class.java
                )
            )
        }

        binding.btnDeviceOta.setOnClickListener {
            val otaManger = LeHomeSdk.getOtaManger()
            if (otaManger != null) {
                otaManger.otaCenter.init(requireActivity().application)
                //
                var devices = ArrayList(LeHomeSdk.getDataManger().allDevices)
                // 选择某一个设备
                var targetNames = ArrayList<String>(devices.map { it.name + "(${it.mac})" })
                //
                MaterialDialog.Builder(requireActivity())
                    .title("选择升级设备")
                    .content("请自行保证设备在线并且在附近")
                    .items(targetNames)
                    .itemsCallback { dialog, itemView, position, text ->

                        val deviceBean = devices[position]
                        otaManger.otaCenter.checkUpgrade(deviceBean, object : OtaCheckListener {
                            override fun hasNewVersion(otaFileBean: OtaFileBean) {
                                askShouldUpdate(deviceBean, otaFileBean)
                            }

                            override fun alreadyNewst() {
                                Toast.makeText(requireContext(), "已经是最新的了", Toast.LENGTH_SHORT)
                                    .show()
                            }

                            override fun fail(msg: String) {
                                // fail
                            }
                        })
                    }
                    .show()
            }
        }

        return root
    }

    private fun askShouldUpdate(deviceBean: DeviceBean, otaFileBean: OtaFileBean) {
        MaterialDialog.Builder(requireActivity())
            .title("请问是否需要升级")
            .content(
                "更新内容：" + otaFileBean.updateDesc +
                        "\n请自行保证设备在线并且在附近"
            )
            .onPositive { dialog, which ->
                //
                val dialog = MaterialDialog.Builder(requireContext())
                    .content("正在连接设备:" + deviceBean.name)
                    .progress(true, 0)
                    .show()
                //
                LeHomeSdk.getOtaManger().otaCenter.upgradeDevices(
                    arrayListOf(deviceBean),
                    otaFileBean,
                    object : OtaListener {
                        override fun upgrading(p0: DeviceBean?, p1: Int) {
                            dialog.setContent("正在升级(${deviceBean.name})：${p1}%")
                        }

                        override fun success(p0: DeviceBean?) {
                            Toast.makeText(requireActivity(), "升级成功", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }

                        override fun fail(p0: DeviceBean?, p1: String?) {
                            Toast.makeText(requireActivity(), "升级失败:$p1", Toast.LENGTH_SHORT).show()
                        }

                        override fun end(p0: String?) {
                        }

                    })
            }
            .positiveText("开始升级")
            .negativeText("取消")
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}