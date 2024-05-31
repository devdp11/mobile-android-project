package com.example.android_studio_project.fragment.location.list_location

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.models.LocationModel
import java.text.SimpleDateFormat
import java.util.Locale

class list_location_adapter(private var locationList: List<LocationModel>, private val onItemClick: (LocationModel) -> Unit) : RecyclerView.Adapter<list_location_adapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val rowLayoutLocations: ConstraintLayout = itemView.findViewById(R.id.rowLayoutLocations)
        val locationName: TextView = itemView.findViewById(R.id.location_name)
        val locationDescription: TextView = itemView.findViewById(R.id.location_description)
        val locationDate: TextView = itemView.findViewById(R.id.location_date)
        val locationRating: TextView = itemView.findViewById(R.id.location_rating)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val clickedTrip = locationList[position]
                onItemClick(clickedTrip)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): list_location_adapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_row_locations, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: list_location_adapter.ViewHolder, position: Int) {
        val currentLocation = locationList[position]
        holder.locationName.text = currentLocation.name ?: "No name"
        holder.locationDescription.text = currentLocation.description ?: "No description"

        val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val dateFormatted = currentLocation.date?.let { dateFormatter.format(it) } ?: "No date"

        holder.locationDate.text = dateFormatted

        holder.locationRating.text = currentLocation.rating?.toString() ?: "No rating"

        if (position % 2 == 0) {
            holder.rowLayoutLocations.setBackgroundColor(holder.itemView.context.getColor(R.color.white))
        } else {
            holder.rowLayoutLocations.setBackgroundColor(holder.itemView.context.getColor(R.color.white))
        }
    }

    override fun getItemCount(): Int {
        return locationList.size
    }

    fun setData(newLocationList: List<LocationModel>) {
        locationList = newLocationList
        notifyDataSetChanged()
    }
}