package com.example.weatherforecast.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.databinding.AlarmItemBinding
import com.example.weatherforecast.databinding.FragmentAlarmBinding
import com.example.weatherforecast.model.Alert
import com.example.weatherforecast.view.AlertRecyclerClickListener
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AlarmAdapter(val alertList:List<Alert>,val clickListener: AlertRecyclerClickListener): RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {
    class AlarmViewHolder(val binding:AlarmItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        return AlarmViewHolder(AlarmItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val simpleDateFormat = SimpleDateFormat("hh:mm")
        val alert=alertList?.get(position)
        var cal= Calendar.getInstance()
        cal.set(Calendar.YEAR, alert.year)
        cal.set(Calendar.MONTH, alert.month)
        cal.set(Calendar.DAY_OF_MONTH, alert.day)
        cal.set(Calendar.HOUR_OF_DAY, alert.hour)
        cal.set(Calendar.MINUTE, alert.minute)

        holder.binding.alertTV.text=alert.alert
        holder.binding.eventTV.text=alert.event
        holder.binding.dateTV.text=DateFormat.getDateInstance().format(cal.time)
        holder.binding.timeTV.text=simpleDateFormat.format(cal.time)
        holder.binding.deleteBtn.setOnClickListener {
            clickListener.deleteAlert(alert.id)
        }
    }

    override fun getItemCount(): Int {
        return alertList.size
    }

}