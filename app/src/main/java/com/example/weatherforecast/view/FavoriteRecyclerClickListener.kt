package com.example.weatherforecast.view

import com.example.weatherforecast.model.Weather

interface FavoriteRecyclerClickListener {
    fun deleteItem(weather: Weather)
    fun onItemLongClick()
}