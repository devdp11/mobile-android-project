package com.example.android_studio_project.fragment.ot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.models.TripModel

class display_home_adapter(private var tripList: List<TripModel>) : RecyclerView.Adapter<display_home_adapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tripName: TextView = itemView.findViewById(R.id.trip_name)
        val tripDescription: TextView = itemView.findViewById(R.id.trip_description)
        val tripStartDate: TextView = itemView.findViewById(R.id.trip_start_date)
        val tripEndDate: TextView = itemView.findViewById(R.id.trip_end_date)
        val tripRating: TextView = itemView.findViewById(R.id.trip_rating)
        val rowLayout: ConstraintLayout = itemView.findViewById(R.id.rowLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentTrip = tripList[position]
        holder.tripName.text = currentTrip.name ?: "No name"
        holder.tripDescription.text = currentTrip.description ?: "No description"
        holder.tripStartDate.text = currentTrip.startDate ?: "No start date"
        holder.tripEndDate.text = currentTrip.endDate ?: "No end date"
        holder.tripRating.text = currentTrip.rating?.toString() ?: "No rating"

        if (position % 2 == 0) {
            holder.rowLayout.setBackgroundColor(holder.itemView.context.getColor(R.color.white))
        } else {
            holder.rowLayout.setBackgroundColor(holder.itemView.context.getColor(R.color.white))
        }
    }

    override fun getItemCount(): Int {
        return tripList.size
    }

    fun setData(newTripList: List<TripModel>) {
        tripList = newTripList
        notifyDataSetChanged()
    }
}
