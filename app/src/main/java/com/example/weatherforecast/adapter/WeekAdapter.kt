package com.example.weatherforecast.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.databinding.DayItemBinding
import com.example.weatherforecast.model.DailyItem
import com.example.weatherforecast.model.Weather
import com.example.weatherforecast.utilities.Utility

class WeekAdapter(var weather: Weather): RecyclerView.Adapter<WeekAdapter.DayItemViewHolder>() {

    class DayItemViewHolder(val binding:DayItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayItemViewHolder {
        return DayItemViewHolder(DayItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: DayItemViewHolder, position: Int) {
        var day= weather.daily?.get(position)
        holder.binding.dateTV.text=""+(Utility.epochToDate((day!!.dt!!)))
        holder.binding.timezoneTV.text=weather.timezone
        holder.binding.weatherDescription.text=day.weather?.get(0)?.description
        holder.binding.weatherIcon.background=Utility.getIcon(holder.itemView.context,day.weather?.get(0)?.icon!!)

    }

    override fun getItemCount(): Int {
        return weather.daily!!.size
    }
}