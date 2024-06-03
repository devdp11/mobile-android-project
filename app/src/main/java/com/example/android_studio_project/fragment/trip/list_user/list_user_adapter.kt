package com.example.android_studio_project.fragment.trip.list_user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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

        Glide.with(holder.itemView.context)
            .load(currentUser.avatar)
            .placeholder(R.drawable.logo)
            .into(holder.userAvatar)

        if (position % 2 == 0) {
            holder.rowLayoutUsers.setBackgroundColor(holder.itemView.context.getColor(R.color.white))
        } else {
            holder.rowLayoutUsers.setBackgroundColor(holder.itemView.context.getColor(R.color.white))
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
