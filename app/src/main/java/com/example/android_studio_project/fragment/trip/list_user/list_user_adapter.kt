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

class list_user_adapter(
    private var userList: List<UserModel>,
    private val onAddUserClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_USER = 0
        private const val VIEW_TYPE_ADD_BUTTON = 1
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rowLayoutUsers: ConstraintLayout = itemView.findViewById(R.id.rowLayoutUsers)
        val userName: TextView = itemView.findViewById(R.id.userName)
        val userEmail: TextView = itemView.findViewById(R.id.userEmail)
        val userAvatar: ImageView = itemView.findViewById(R.id.userAvatar)
    }

    inner class AddUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rowLayoutUsers: ConstraintLayout = itemView.findViewById(R.id.rowLayoutUsers)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == userList.size) VIEW_TYPE_ADD_BUTTON else VIEW_TYPE_USER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_row_users, parent, false)
        return if (viewType == VIEW_TYPE_USER) {
            UserViewHolder(view)
        } else {
            AddUserViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_USER) {
            val userHolder = holder as UserViewHolder
            val currentUser = userList[position]
            userHolder.userName.text = currentUser.username ?: "No username"
            userHolder.userEmail.text = currentUser.email ?: "No email"
            userHolder.userAvatar.loadBase64Image(currentUser.avatar)


        } else {
            val addUserHolder = holder as AddUserViewHolder
            addUserHolder.rowLayoutUsers.setOnClickListener {
                onAddUserClick()
            }
            addUserHolder.rowLayoutUsers.findViewById<ImageView>(R.id.userAvatar).setImageResource(R.drawable.add_trip) // Icon for add button
            addUserHolder.rowLayoutUsers.findViewById<TextView>(R.id.userEmail).visibility = View.GONE
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
        return userList.size + 1
    }

    fun setData(newUserList: List<UserModel>) {
        userList = newUserList
        notifyDataSetChanged()
    }
}
