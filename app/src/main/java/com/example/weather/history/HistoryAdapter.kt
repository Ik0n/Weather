package com.example.weather.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.model.Weather
import kotlinx.android.synthetic.main.history_layout_recycler_item.view.*

class HistoryAdapter(
    private var onItemViewClick: OnItemViewClick?
) : RecyclerView.Adapter<HistoryAdapter.RecyclerItemViewHolder>() {

    private var data: List<Weather> = arrayListOf()

    fun setData(data: List<Weather>) {
        this.data = data
        notifyDataSetChanged()
    }

    interface OnItemViewClick {
        fun onItemViewClick(weather: Weather)
    }

    inner class RecyclerItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(data: Weather) {
            if (layoutPosition!= RecyclerView.NO_POSITION) {
                itemView.historyLayoutRecyclerViewItem.text =
                    String.format("%s %d %s", data.city.city, data.temperature, data.condition)
                itemView.setOnClickListener {
                    Toast.makeText(
                        itemView.context,
                        data.city.city,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                itemView.apply {
                    setOnClickListener { onItemViewClick?.onItemViewClick(data) }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerItemViewHolder {
        return RecyclerItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.history_layout_recycler_item, parent, false) as View
        )
    }

    override fun onBindViewHolder(holder: RecyclerItemViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

}