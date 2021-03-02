package com.example.weatherforecast.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


data class Weather(

	@field:SerializedName("alerts")
	val alerts: List<AlertsItem?>? = null,

	@field:SerializedName("current")
	val current: Current? = null,

	@field:SerializedName("timezone")
	val timezone: String? = null,

	@field:SerializedName("timezone_offset")
	val timezoneOffset: Int? = null,

	@field:SerializedName("daily")
	val daily: List<DailyItem?>? = null,

	@field:SerializedName("lon")
	val lon: Double? = null,

	@field:SerializedName("hourly")
	val hourly: List<HourlyItem?>? = null,

	@field:SerializedName("lat")
	val lat: Double? = null
) : Parcelable {
	constructor(parcel: Parcel) : this(
		TODO("alerts"),
		TODO("current"),
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		TODO("daily"),
		parcel.readValue(Double::class.java.classLoader) as? Double,
		TODO("hourly"),
		parcel.readValue(Double::class.java.classLoader) as? Double
	) {
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(timezone)
		parcel.writeValue(timezoneOffset)
		parcel.writeValue(lon)
		parcel.writeValue(lat)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<Weather> {
		override fun createFromParcel(parcel: Parcel): Weather {
			return Weather(parcel)
		}

		override fun newArray(size: Int): Array<Weather?> {
			return arrayOfNulls(size)
		}
	}
}