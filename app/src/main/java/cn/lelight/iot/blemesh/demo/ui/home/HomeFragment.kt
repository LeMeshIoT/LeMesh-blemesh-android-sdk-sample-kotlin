package cn.lelight.iot.blemesh.demo.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cn.lelight.iot.blemesh.demo.BleMeshDemoInstance
import cn.lelight.iot.blemesh.demo.databinding.FragmentHomeBinding
import cn.lelight.iot.blemesh.demo.ui.adddevice.AddDevicesActivity
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

        BleMeshDemoInstance.get().msg.observe(viewLifecycleOwner){
            binding.textHome2.text = it
            materialDialog?.dismiss()
        }

        binding.btnAddDevice.setOnClickListener {
            // 添加设备
            requireActivity().startActivity(
                Intent(
                    requireActivity(),
                    AddDevicesActivity::class.java
                )
            )
        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}