package com.example.weatherforecast.view

import afu.org.checkerframework.checker.igj.qual.Mutable
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentAddAlarmBinding
import java.text.DateFormat
import java.util.*
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.model.Alert
import com.example.weatherforecast.model.Constants
import com.example.weatherforecast.receiver.AlertReceiver
import com.example.weatherforecast.utilities.Utility
import com.example.weatherforecast.viewmodel.AlarmViewModel
import kotlinx.coroutines.*
import org.antlr.v4.runtime.misc.MurmurHash.finish

class AddAlarmFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    lateinit var calendar: Calendar
    lateinit var doneAll: MutableLiveData<Boolean>
    var doneDate=false
    var doneTime=false
    var doneEvent=false
    var doneAlert=false
    lateinit var viewModel:AlarmViewModel
    lateinit var alert:Alert
    lateinit var alerts:String
    lateinit var events:String
    private var _binding: FragmentAddAlarmBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddAlarmBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(Utility.isMorning()){
            binding.addBtn.backgroundTintList=context?.resources?.getColorStateList(com.example.weatherforecast.R.color.sun)
        }else{
            binding.addBtn.backgroundTintList=context?.resources?.getColorStateList(com.example.weatherforecast.R.color.night)
        }

        viewModel=ViewModelProvider(this).get(AlarmViewModel::class.java)

        calendar= Calendar.getInstance()

        binding.addBtn.isEnabled=false
        doneAll=MutableLiveData<Boolean>()
        doneAll.postValue(false)

        doneAll.observe(viewLifecycleOwner,{
            if(doneAlert==true&&doneDate==true&&doneEvent==true&&doneTime==true){
                binding.addBtn.isEnabled=true
            }
        })

        binding.addBtn.setOnClickListener {
            if(viewModel.isValidTime(calendar)) {
                alert = Alert(
                    year = calendar.get(Calendar.YEAR),
                    month = calendar.get(Calendar.MONTH),
                    day = calendar.get(Calendar.DAY_OF_MONTH),
                    hour = calendar.get(Calendar.HOUR_OF_DAY),
                    minute = calendar.get(Calendar.MINUTE),
                    event = events,
                    alert = alerts
                )
                var id = 0L
                CoroutineScope(Dispatchers.IO).launch {
                    id = viewModel.insertAlert(alert)
                    viewModel.setAlarm(calendar, alert, id,requireContext())
                    withContext(Dispatchers.Main) {
                        activity?.onBackPressed()
                    }
                }
            }
            else{
                Toast.makeText(requireContext(),R.string.validTime,Toast.LENGTH_LONG).show()
            }
        }

        val eventsArray=ArrayAdapter(requireContext(),R.layout.spinner_item,resources.getStringArray(R.array.eventsArr))
        binding.eventSpinner.adapter=eventsArray

        val alertsArray=ArrayAdapter(requireContext(),R.layout.spinner_item,resources.getStringArray(R.array.alertsArr))
        binding.alertSpinner.adapter=alertsArray

        binding.eventSpinner.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(parent?.getItemAtPosition(position).toString()){
                   "clear sky"->{
                       events="clear sky"
                       true
                   }
                    "few clouds"->{
                        events="few clouds"
                        true
                    }
                    "scattered clouds"->{
                        events="scattered clouds"
                        true
                    }
                    "broken clouds"->{
                        events="broken clouds"
                        true
                    }
                    "shower rain"->{
                        events="shower rain"
                        true
                    }
                    "rain"->{
                        events="rain"
                        true
                    }
                    "thunderstorm"->{
                        events="thunderstorm"
                        true
                    }
                    "snow"->{
                        events="snow"
                        true
                    }
                    "mist"->{
                        events="mist"
                        true
                    }
                }
               doneEvent=true
                doneAll.postValue(true)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
        binding.alertSpinner.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(parent?.getItemAtPosition(position).toString()){
                    "Alarm"->{
                        alerts="Alarm"
                       true
                    }
                    "Notification"->{
                        alerts="Notification"
                        true
                    }
                }
                doneAlert=true
                doneAll.postValue(true)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
        binding.dateBtn.setOnClickListener {
            DatePickerDialog(
                requireContext(), this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.timeBtn.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                is24HourFormat(requireContext())
            ).show()
        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        binding.dateTV.text=DateFormat.getDateInstance().format(calendar.time)
        doneDate=true
        doneAll.postValue(true)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        binding.timeTV.text=DateFormat.getTimeInstance().format(calendar.time)
        doneTime=true
        doneAll.postValue(true)
    }

//    private fun setAlarm(calendar: Calendar,alert:Alert,id:Long) {
//        var alarmManager = activity?.getSystemService(ALARM_SERVICE) as AlarmManager
//        var broadcastIntent = Intent(requireContext(), DialogActivity::class.java)
//        broadcastIntent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        broadcastIntent.putExtra(Constants.alertID,id.toInt())
//        broadcastIntent.putExtra(Constants.ALERT, alert.alert)
//        broadcastIntent.putExtra(Constants.EVENT, alert.event)
//        var pendingBroadcastIntent = PendingIntent.getActivity(
//            requireContext(), id.toInt(),
//            broadcastIntent, 0
//        )
//        alarmManager.setExact(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            pendingBroadcastIntent
//        )
//    }
//    fun isValidTime():Boolean{
//    var currentTime=Utility.getCurrentDateAndTime()
//        return if(currentTime.after(calendar)){
//            false
//        }else{
//            true
//        }
//    }
}
