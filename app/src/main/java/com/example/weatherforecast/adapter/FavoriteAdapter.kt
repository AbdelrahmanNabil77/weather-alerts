package com.example.weatherforecast.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.findFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.NavGraphDirections
import com.example.weatherforecast.databinding.FavoriteItemBinding
import com.example.weatherforecast.model.Weather
import com.example.weatherforecast.utilities.Utility
import com.example.weatherforecast.view.FavoriteRecyclerClickListener
import com.example.weatherforecast.view.HomeFragmentDirections
import com.example.weatherforecast.viewmodel.FavoriteViewModel
import com.example.weatherforecast.viewmodel.HomeViewModel
import okhttp3.internal.Util
import java.text.DateFormat
import java.text.SimpleDateFormat

class FavoriteAdapter(var weatherList:List<Weather>,var recyclerClickListener: FavoriteRecyclerClickListener): RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {
    class FavoriteViewHolder(val binding:FavoriteItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        return FavoriteViewHolder(FavoriteItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
       var weather = weatherList?.get(position)
        val simpleDateFormat = SimpleDateFormat("hh:mm")
        holder.binding.tempUnit.text=Utility.getTempUnit(holder.itemView.context)
        holder.binding.windSpeedUnit.text=Utility.getWindSpeedUnit(holder.itemView.context)
        holder.binding.tempTV.text = weather.current?.temp.toString()
        holder.binding.dateTV.text = Utility.getDate(weather.current!!.dt!!.toInt(),weather.timezoneOffset!!)//""+Utility.epochToDate(weather.current!!.dt!!.toInt()) //DateFormat.getDateInstance().format(Utility.getCurrentDateAndTime().time)
        holder.binding.timeTV.text = Utility.getDayTime(weather.current!!.dt!!.toInt(),weather.timezoneOffset!!)//""+Utility.epochToTime(weather.current!!.dt!!.toInt())//simpleDateFormat.format(Utility.getCurrentDateAndTime().time)
        holder.binding.humidityTV.text = weather.current?.humidity.toString()
        holder.binding.windSpeedTV.text = weather.current?.windSpeed.toString()
        holder.binding.pressureTV.text = weather.current?.pressure.toString()
        holder.binding.cloudTV.text = weather.current?.clouds.toString()
        holder.binding.descriptionTV.text = weather.current?.weather?.get(0)?.description
        holder.binding.timezoneTV.text = weather.timezone
        holder.binding.weatherIcon.background = Utility.getIcon(holder.itemView.context, weather.current?.weather!![0]?.icon.toString())
        holder.binding.hoursForecast.setOnClickListener {
            if(weather!=null){
                val action = NavGraphDirections.actionGlobalHoursFragment(weather!!)
                findNavController(it.findFragment()).navigate(action)
            }
        }

        //ViewCompat.setNestedScrollingEnabled(holder.binding.hoursRecView,true);
        //holder.binding.hoursRecView.onInterceptTouchEvent()
        if(Utility.isMorning()){
            holder.binding.weekForecast.backgroundTintList=holder.itemView.context?.resources?.getColorStateList(com.example.weatherforecast.R.color.sun)
            holder.binding.hoursForecast.backgroundTintList=holder.itemView.context?.resources?.getColorStateList(com.example.weatherforecast.R.color.sun)
        }else{
            holder.binding.weekForecast.backgroundTintList=holder.itemView.context?.resources?.getColorStateList(com.example.weatherforecast.R.color.night)
            holder.binding.hoursForecast.backgroundTintList=holder.itemView.context?.resources?.getColorStateList(com.example.weatherforecast.R.color.night)
        }
        holder.binding.deleteBtn.setOnClickListener {
            recyclerClickListener.deleteItem(weather)
        }
        holder.binding.weekForecast.setOnClickListener {
            val action=HomeFragmentDirections.actionGlobalDaysFragment(weather!!)
            findNavController(it.findFragment()).navigate(action)
        }

    }

    override fun getItemCount(): Int {
        return weatherList.size
    }

}