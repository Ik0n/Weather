package com.example.weather.view.details

import android.text.format.Time
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.model.Forecasts
import com.example.weather.model.ForecastsDTO
import com.example.weather.model.Hour
import com.example.weather.model.HourDTO
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.details_recycler_item.view.*

class HoursTempAdapter : RecyclerView.Adapter<HoursTempAdapter.HourTempHolder>() {

    private var hoursTemp : List<Hour>? = listOf()

    inner class HourTempHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(hours: Hour?) {
            itemView.apply {
                hours?.let { hours ->
                    hours.hour?.let { hour ->
                        findViewById<TextView>(R.id.details_recycler_time).text =
                            if (hour.toInt() < 10) "0${hour}:00" else "${hour}:00"
                    }
                    hours.temp?.let { temp ->
                        findViewById<TextView>(R.id.details_recycler_temp).text =
                            if (temp > 0) "+${temp}°С" else "${temp}°C"
                    }
                    hours.icon.let { icon ->
                        Glide
                            .with(context)
                            .load("https://yastatic.net/weather/i/icons/funky/dark/$icon.svg")
                            .into(details_recycler_hours_icon)
                    }

                }

            }
        }
    }

    fun setHoursTemp(data: Forecasts) {
        hoursTemp = data.hours
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HourTempHolder(
            LayoutInflater.from(parent.context).inflate(
                    R.layout.details_recycler_item,
                    parent,
                    false
                ) as View
        )

    override fun onBindViewHolder(holder: HourTempHolder, position: Int) {
        holder.bind(hoursTemp?.get(position))
    }

    override fun getItemCount() = hoursTemp?.size ?: 0

}