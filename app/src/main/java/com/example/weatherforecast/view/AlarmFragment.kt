package com.example.weatherforecast.view

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.weatherforecast.R
import com.example.weatherforecast.adapter.AlarmAdapter
import com.example.weatherforecast.databinding.FragmentAlarmBinding
import com.example.weatherforecast.model.Alert
import com.example.weatherforecast.model.Constants
import com.example.weatherforecast.utilities.Utility
import com.example.weatherforecast.viewmodel.AlarmViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class AlarmFragment : Fragment(),AlertRecyclerClickListener{
    lateinit var viewModel:AlarmViewModel
    private var _binding: FragmentAlarmBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=ViewModelProvider(this).get(AlarmViewModel::class.java)
        viewModel.getAlertsList().observe(viewLifecycleOwner,{
            if(it!=null){
                binding.alarmRecView.adapter=AlarmAdapter(it,this)
            }
        })
        if(Utility.isMorning()){
            binding.addBtn.backgroundTintList=context?.resources?.getColorStateList(com.example.weatherforecast.R.color.sun)

        }else{
            binding.addBtn.backgroundTintList=context?.resources?.getColorStateList(com.example.weatherforecast.R.color.night)
        }

        binding.addBtn.setOnClickListener {
            val sharedPref= requireContext().getSharedPreferences(Constants.sharedPrefLocation, Context.MODE_PRIVATE)
            val lat=sharedPref.getString(Constants.sharedPrefLat,null)
            val lon=sharedPref.getString(Constants.sharedPrefLon,null)
            if(lat!=null&&lon!=null) {
                val action = AlarmFragmentDirections.actionAlarmFragmentToAddAlarmFragment()
                findNavController().navigate(action)
            }else{
                Toast.makeText(requireContext(),R.string.addAlarmToast,Toast.LENGTH_LONG).show()
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun cancelAlarm(id:Int) {
        var alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var broadcastIntent = Intent(requireContext(), DialogActivity::class.java)
        broadcastIntent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        var pendingBroadcastIntent = PendingIntent.getActivity(
            requireContext(), id.toInt(),
            broadcastIntent, 0
        )
        alarmManager.cancel(pendingBroadcastIntent)
    }
    override fun deleteAlert(id:Int) {
        val dialog= AlertDialog.Builder(requireContext()).setTitle(R.string.warning).setMessage(R.string.deleteAlarmDialog).setPositiveButton(R.string.yes) { dialog, which ->
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.deleteAlert(id)
                cancelAlarm(id)
            }
        }.setNegativeButton(R.string.no, DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        }).setCancelable(false).show()

    }
}