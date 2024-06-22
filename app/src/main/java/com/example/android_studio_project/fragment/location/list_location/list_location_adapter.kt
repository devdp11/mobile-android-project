package com.example.android_studio_project.fragment.location.list_location

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.models.LocationModel
import com.example.android_studio_project.data.retrofit.services.LocationService
import java.text.SimpleDateFormat
import java.util.Locale

class list_location_adapter(
    private val context: Context,
    private var locationList: List<LocationModel>,
    private val onItemClick: (LocationModel) -> Unit
) : RecyclerView.Adapter<list_location_adapter.ViewHolder>() {

    private val locationService = LocationService(context)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val rowLayoutLocations: ConstraintLayout = itemView.findViewById(R.id.rowLayoutLocations)
        val locationName: TextView = itemView.findViewById(R.id.location_name)
        val locationDate: TextView = itemView.findViewById(R.id.location_date)
        val locationRating: RatingBar = itemView.findViewById(R.id.location_rating)
        val locationImage: ImageView = itemView.findViewById(R.id.location_image)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val clickedLocation = locationList[position]
                onItemClick(clickedLocation)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_row_locations, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentLocation = locationList[position]
        holder.locationName.text = currentLocation.name ?: "No name"

        val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val dateFormatted = currentLocation.date?.let { dateFormatter.format(it) } ?: "No date"
        holder.locationDate.text = dateFormatted

        holder.locationRating.rating = currentLocation.rating ?: 0f

        currentLocation.uuid?.let { locationId ->
            locationService.getPhotoByLocationId(locationId, { photoList ->
                if (!photoList.isNullOrEmpty()) {
                    val photo = photoList.firstOrNull()
                    val imageBytes = Base64.decode(photo?.data ?: "", Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    holder.locationImage.setImageBitmap(bitmap)
                    holder.locationImage.visibility = View.VISIBLE
                } else {
                    holder.locationImage.visibility = View.GONE
                }
            }, { error ->
                holder.locationImage.visibility = View.GONE
            })
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
