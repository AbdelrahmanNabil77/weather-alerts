package com.example.weatherforecast.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.databinding.HourItemBinding
import com.example.weatherforecast.model.HourlyItem
import com.example.weatherforecast.model.Weather
import com.example.weatherforecast.utilities.Utility

class HourAdapter(val weather: Weather):RecyclerView.Adapter<HourAdapter.HourViewHolder>() {
    val hourList = weather.hourly
    class HourViewHolder(val binding:HourItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        return HourViewHolder(HourItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        var hrs=hourList?.get(position)
        holder.binding.tempTV.text=""+hrs?.temp
        holder.binding.tempUnit.text=Utility.getTempUnit(holder.itemView.context)
        holder.binding.timeTV.text=Utility.getDayTime(hrs!!.dt!!.toInt(),weather.timezoneOffset!!)//""+Utility.epochToTime(hrs?.dt?.toInt()!!)
        holder.binding.weatherIcon.background=Utility.getIcon(holder.itemView.context,hrs.weather?.get(0)?.icon!!)
        holder.binding.weatherDescription.text=hrs.weather?.get(0)?.description
        holder.binding.humidityTV.text=""+hrs?.humidity
        holder.binding.windSpeedTV.text=""+hrs?.windSpeed
        holder.binding.windSpeedUnit.text=Utility.getWindSpeedUnit(holder.itemView.context)
    }

    override fun getItemCount(): Int {
        return 24
    }
}