package com.example.weather.view.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.model.getHoursTemp

class HoursTempAdapter : RecyclerView.Adapter<HoursTempAdapter.HourTempHolder>() {

    private val hoursTemp = getHoursTemp().toList()

    inner class HourTempHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(pair: Pair<String, Int>) {
            itemView.apply {
                findViewById<TextView>(R.id.details_recycler_time).text = pair.first
                findViewById<TextView>(R.id.details_recycler_temp).text = pair.second.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HourTempHolder(
            LayoutInflater.from(parent.context).inflate(
                    R.layout.details_recycler_item,
                    parent,
                    false
                ) as View
        )

    override fun onBindViewHolder(holder: HourTempHolder, position: Int) {
        holder.bind(hoursTemp[position])
    }

    override fun getItemCount() = hoursTemp.size

}