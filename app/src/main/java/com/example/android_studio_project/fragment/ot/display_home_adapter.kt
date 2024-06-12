package com.example.android_studio_project.fragment.ot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.models.TripModel
import java.text.SimpleDateFormat
import java.util.Locale
import android.widget.RatingBar

class display_home_adapter(private var tripList: List<TripModel>, private val onItemClick: (TripModel) -> Unit) : RecyclerView.Adapter<display_home_adapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val tripName: TextView = itemView.findViewById(R.id.trip_name)
        val tripStartDate: TextView = itemView.findViewById(R.id.trip_start_date)
        val tripEndDate: TextView = itemView.findViewById(R.id.trip_end_date)
        val tripRating: RatingBar = itemView.findViewById(R.id.trip_rating)
        val rowLayout: ConstraintLayout = itemView.findViewById(R.id.rowLayout)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val clickedTrip = tripList[position]
                onItemClick(clickedTrip)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_row_trips, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentTrip = tripList[position]
        holder.tripName.text = currentTrip.name ?: "No name"

        val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val startDateFormatted = currentTrip.startDate?.let { dateFormatter.format(it) } ?: "No start date"
        val endDateFormatted = currentTrip.endDate?.let { dateFormatter.format(it) } ?: "No end date"

        holder.tripStartDate.text = startDateFormatted
        holder.tripEndDate.text = endDateFormatted

        holder.tripRating.rating = currentTrip.rating ?: 0f


    }

    override fun getItemCount(): Int {
        return tripList.size
    }

    fun updateData(newTripList: List<TripModel>) {
        tripList = newTripList
        notifyDataSetChanged()
    }

    fun setData(newTripList: List<TripModel>) {
        tripList = newTripList
        notifyDataSetChanged()
    }
}
