package com.example.weatherforecast.model

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.TypeConverters

import com.example.weatherforecast.datalayer.converters.AlertsItemTypeConverter
import com.example.weatherforecast.datalayer.converters.CurrentTypeConverter
import com.example.weatherforecast.datalayer.converters.DailyItemTypeConverter
import com.example.weatherforecast.datalayer.converters.HourlyItemTypeConverter
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys= ["lon","lat"])
@JvmSuppressWildcards
@TypeConverters(AlertsItemTypeConverter::class,DailyItemTypeConverter::class,HourlyItemTypeConverter::class,CurrentTypeConverter::class)
data class Weather(

	@field:SerializedName("alerts")
	@ColumnInfo(name="alerts")
	val alerts: List<AlertsItem?>? = null,

	@field:SerializedName("current")
	@ColumnInfo(name="current")
	val current: Current? = null,

	@field:SerializedName("timezone")
	@ColumnInfo(name="timezone")
	val timezone: String? = null,

	@field:SerializedName("timezone_offset")
	@ColumnInfo(name="timezone_offset")
	val timezoneOffset: Int? = null,

	@field:SerializedName("daily")
	@ColumnInfo(name="daily")
	val daily: List<DailyItem?>? = null,

	@field:SerializedName("lon")
	@ColumnInfo(name="lon")
	@NonNull val lon: Double,

	@field:SerializedName("hourly")
	@ColumnInfo(name="hourly")
	val hourly: List<HourlyItem?>? = null,

	@field:SerializedName("lat")
	@ColumnInfo(name="lat")
	@NonNull val lat: Double
) : Parcelable {
	constructor(parcel: Parcel) : this(
			TODO("alerts"),
			TODO("current"),
			parcel.readString(),
			parcel.readValue(Int::class.java.classLoader) as? Int,
			TODO("daily"),
		(parcel.readValue(Double::class.java.classLoader) as? Double)!!,
			TODO("hourly"),
		(parcel.readValue(Double::class.java.classLoader) as? Double)!!
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