/**
 * Created by Taiwo Farinu on 06-Aug-21
 */

package com.app.demo.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.app.demo.R
import com.app.demo.databinding.NotificationItemBinding
import com.app.demo.model.Notification
import com.app.demo.model.Post
import com.app.demo.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class NotificationAdapter(private val notifications: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(private val itemBinding: NotificationItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(notification: Notification) {
            getUser(itemBinding.profileImage, itemBinding.username, notification.userid)
            itemBinding.comment.text = notification.text
            if (notification.isPost) {
                itemBinding.postImage.visibility = View.VISIBLE
                getPostImage(itemBinding.postImage, notification.postid)
            } else {
                itemBinding.postImage.visibility = View.GONE
            }

            itemBinding.root.setOnClickListener { view ->
                if (notification.isPost) {
                    itemBinding.root.context.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                        .edit().putString("postid", notification.postid).apply()
                    view.findNavController()
                        .navigate(R.id.action_notificationFragment_to_postDetailFragment)
                } else {
                    itemBinding.root.context.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                        .edit().putString("profileId", notification.userid).apply()
                    view.findNavController()
                        .navigate(R.id.action_notificationFragment_to_profileFragment)
                }
            }
        }

        private fun getPostImage(postImage: ImageView, postId: String) {
            FirebaseDatabase.getInstance().reference.child("Posts").child(postId)
                .addValueEventListener(
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val post = dataSnapshot.getValue(Post::class.java)!!
                            Log.d("NotificationAdapter img", post.imageurl)
                            Picasso.get().load(post.imageurl).placeholder(R.mipmap.ic_launcher)
                                .into(postImage)
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
        }

        private fun getUser(profileImage: ImageView, username: TextView, userId: String) {
            FirebaseDatabase.getInstance().reference.child("Users").child(userId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val user = dataSnapshot.getValue(User::class.java)!!
                        if (user.imageurl == "default") {
                            profileImage.setImageResource(R.mipmap.ic_launcher)
                        } else {
                            Picasso.get().load(user.imageurl).into(profileImage)
                        }
                        username.text = user.username

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding =
            NotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]
        holder.bind(notification)
    }

    override fun getItemCount() = notifications.size
}