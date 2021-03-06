package cn.lelight.iot.blemesh.demo.ui.home

import android.content.Intent
import android.os.Bundle
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
import cn.lelight.leiot.sdk.utils.ShareUtils
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems


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

        BleMeshDemoInstance.get().reSendTime.observe(viewLifecycleOwner) {
            ShareUtils.getInstance().setValue("reSendTime", it)
            binding.etResend.setText(it.toString())
            binding.etResend.setSelection(it.toString().length)
        }

        BleMeshDemoInstance.get().isEachControl.observe(viewLifecycleOwner) {
            ShareUtils.getInstance().setValue("isEachControl", it)
            binding.tbtnGroupEach.isChecked = it
        }

        BleMeshDemoInstance.get().subDevTime.observe(viewLifecycleOwner) {
            ShareUtils.getInstance().setValue("subDevTime", it)
            binding.etSubtime.setText(it.toString())
            binding.etSubtime.setSelection(it.toString().length)
        }

        BleMeshDemoInstance.get().reScanTime.observe(viewLifecycleOwner) {
            ShareUtils.getInstance().setValue("reScanTime", it)
            binding.etRescan.setText(it.toString())
            binding.etRescan.setSelection(it.toString().length)
        }

        //
        binding.btnResend.setOnClickListener {
            if (LeHomeSdk.getBleLeMeshManger() == null) {
                Toast.makeText(requireContext(), "????????????/????????????", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //
            var resent = binding.etRescan.text.toString().toInt()
            if (resent >= 0) {
                LeHomeSdk.getBleLeMeshManger().reSendTime = resent
                BleMeshDemoInstance.get().reScanTime.value = resent
            }
        }

        binding.tbtnGroupEach.setOnCheckedChangeListener { compoundButton, b ->
            if (LeHomeSdk.getBleLeMeshManger() == null) {
                Toast.makeText(requireContext(), "????????????/????????????", Toast.LENGTH_SHORT).show()
                return@setOnCheckedChangeListener
            }
            //
            LeHomeSdk.getBleLeMeshManger().setGroupControlEachAgain(b)
            BleMeshDemoInstance.get().isEachControl.value = b
        }

        binding.tbtnOnlyControl.setOnCheckedChangeListener { compoundButton, b ->
            if (LeHomeSdk.getBleLeMeshManger() == null) {
                Toast.makeText(requireContext(), "????????????/????????????", Toast.LENGTH_SHORT).show()
                return@setOnCheckedChangeListener
            }
            //
            LeHomeSdk.getBleLeMeshManger().setOnlyControlProperty(b)
        }

        binding.btnHeartbeat.setOnClickListener {
            if (LeHomeSdk.getBleLeMeshManger() == null) {
                Toast.makeText(requireContext(), "????????????/????????????", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //
            var value = binding.etSubtime.text.toString().toInt()
            if (value >= 0) {
                LeHomeSdk.getBleLeMeshManger().setSubDevHeartbeatTime(value)
                BleMeshDemoInstance.get().subDevTime.value = value
                //
                Toast.makeText(requireContext(), "????????????", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRescan.setOnClickListener {
            if (LeHomeSdk.getBleLeMeshManger() == null) {
                Toast.makeText(requireContext(), "????????????/????????????", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //
            var value = binding.etRescan.text.toString().toInt()
            if (value >= 0) {
                LeHomeSdk.getBleLeMeshManger().setCheckScanSubDevTime(value)
                BleMeshDemoInstance.get().reScanTime.value = value
                //
                Toast.makeText(requireContext(), "????????????", Toast.LENGTH_SHORT).show()
            }
        }

        BleMeshDemoInstance.get().msg.observe(viewLifecycleOwner) {
            binding.textHome2.text = it
            materialDialog?.dismiss()
        }

        if (LeHomeSdk.getBleLeMeshManger() != null) {
            binding.textHome.text =
                "ver:${LeHomeSdk.getBleLeMeshManger().version}\n${BuildConfig.BUILDTIME}"
        }

        binding.btnAddDevice.setOnClickListener {
            // ????????????
            if (LeHomeSdk.getBleLeMeshManger() == null) {
                Toast.makeText(requireContext(), "????????????/????????????", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            requireActivity().startActivity(
                Intent(
                    requireActivity(),
                    AddDevicesActivity::class.java
                )
            )
        }

        binding.btnAddDevice.setOnLongClickListener {
            // ????????????
            if (LeHomeSdk.getBleLeMeshManger() == null) {
                Toast.makeText(requireContext(), "????????????/????????????", Toast.LENGTH_SHORT).show()
                return@setOnLongClickListener true
            }

            MaterialDialog(requireContext())
                .show {
                    title(text = "??????")
                    message(text = "???????????????????????????")
                    input(allowEmpty = false) { materialDialog, charSequence ->
                        requireActivity().startActivity(
                            Intent(
                                requireActivity(),
                                AddDevicesActivity::class.java
                            ).apply {
                                putExtra("type", charSequence.toString())
                            }
                        )
                    }
                }


            true
        }

        binding.btnDeviceOta.setOnClickListener {
            val otaManger = LeHomeSdk.getOtaManger()
            if (otaManger != null) {
                //
                var devices = ArrayList(LeHomeSdk.getDataManger().allDevices)
                // ?????????????????????
                var targetNames = ArrayList<String>(devices.map { it.name + "(${it.mac})" })
                //
                MaterialDialog(requireActivity()).show {
                    title(text = "??????????????????")
                    message(text = "??????????????????????????????????????????")
                    listItems(items = targetNames) { dialog, index, text ->
                        val deviceBean = devices[index]
                        otaManger.otaCenter.checkUpgrade(deviceBean, object : OtaCheckListener {
                            override fun hasNewVersion(otaFileBean: OtaFileBean) {
                                askShouldUpdate(deviceBean, otaFileBean)
                            }

                            override fun alreadyNewst() {
                                Toast.makeText(requireContext(), "?????????????????????", Toast.LENGTH_SHORT)
                                    .show()
                            }

                            override fun fail(msg: String) {
                                // fail
                            }
                        })
                    }
                }
            }
        }

        return root
    }

    private fun askShouldUpdate(deviceBean: DeviceBean, otaFileBean: OtaFileBean) {
        MaterialDialog(requireContext()).show {
            title(text = "????????????????????????")
            message(
                text = "???????????????" + otaFileBean.updateDesc +
                        "\n??????????????????????????????????????????"
            )
            positiveButton(text = "????????????") {
                val dialog = MaterialDialog(requireContext()).show {
                    message(text = "??????????????????:" + deviceBean.name) { }
                }

                //
                LeHomeSdk.getOtaManger().otaCenter.upgradeDevices(
                    arrayListOf(deviceBean),
                    otaFileBean,
                    object : OtaListener {
                        override fun upgrading(p0: DeviceBean?, p1: Int) {
                            dialog.message(text = "????????????(${deviceBean.name})???${p1}%")
                        }

                        override fun success(p0: DeviceBean?) {
                            Toast.makeText(requireActivity(), "????????????", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }

                        override fun fail(p0: DeviceBean?, p1: String?) {
                            Toast.makeText(requireActivity(), "????????????:$p1", Toast.LENGTH_SHORT).show()
                        }

                        override fun end(p0: String?) {
                        }

                    })
            }
            negativeButton(text = "??????")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}