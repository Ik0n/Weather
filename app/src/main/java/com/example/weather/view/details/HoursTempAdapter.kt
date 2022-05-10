package com.example.weather.view.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.model.ForecastsDTO
import com.example.weather.model.HourDTO
import com.example.weather.model.Weather
import com.example.weather.model.getHoursTemp
import com.squareup.picasso.Picasso

class HoursTempAdapter : RecyclerView.Adapter<HoursTempAdapter.HourTempHolder>() {

    private var hoursTemp : List<HourDTO>? = listOf()

    inner class HourTempHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(hourDTO: HourDTO?) {
            itemView.apply {
                findViewById<TextView>(R.id.details_recycler_time).text = hourDTO?.hour.toString()
                findViewById<TextView>(R.id.details_recycler_temp).text = hourDTO?.temp.toString()

                val temp = "https://yastatic.net/weather/i/icons/funky/dark/" + hourDTO?.icon + ".svg"
                Picasso.get().load(temp).into(findViewById<ImageView>(R.id.details_recycler_hours_icon))
            }
        }
    }

    fun setHoursTemp(data: ForecastsDTO?) {
        hoursTemp = data?.hours
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