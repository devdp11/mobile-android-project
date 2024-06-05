package com.example.android_studio_project.fragment.trip.list_user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.util.Base64
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.models.UserModel

class list_user_adapter(private var userList: List<UserModel>) : RecyclerView.Adapter<list_user_adapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rowLayoutUsers: ConstraintLayout = itemView.findViewById(R.id.rowLayoutUsers)
        val userName: TextView = itemView.findViewById(R.id.userName)
        val userAvatar: ImageView = itemView.findViewById(R.id.userAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): list_user_adapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_row_users, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: list_user_adapter.ViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.userName.text = currentUser.username ?: "No username"

        holder.userAvatar.loadBase64Image(currentUser.avatar)

        if (position % 2 == 0) {
            holder.rowLayoutUsers.setBackgroundColor(holder.itemView.context.getColor(R.color.white))
        } else {
            holder.rowLayoutUsers.setBackgroundColor(holder.itemView.context.getColor(R.color.white))
        }
    }

    private fun ImageView.loadBase64Image(base64String: String?) {
        if (base64String.isNullOrEmpty()) {
            Glide.with(this.context)
                .load(R.drawable.default_image)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(this)
        } else {
            val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
            Glide.with(this.context)
                .load(imageBytes)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .placeholder(R.drawable.default_image)
                .into(this)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun setData(newUserList: List<UserModel>) {
        userList = newUserList
        notifyDataSetChanged()
    }
}
