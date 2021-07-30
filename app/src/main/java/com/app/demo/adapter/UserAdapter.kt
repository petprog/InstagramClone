/**
 * Created by Taiwo Farinu on 27-Jul-21
 */

package com.app.demo.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.app.demo.R
import com.app.demo.databinding.UserItemBinding
import com.app.demo.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class UserAdapter(private val users: List<User>, var isFragment: Boolean) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private lateinit var firebaseUser: FirebaseUser

    inner class ViewHolder(private val itemBinding: UserItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(user: User) {
            itemBinding.apply {
                fullname.text = user.name
                username.text = user.username
                btnFollow.visibility = View.VISIBLE
                Log.d("ImageUrl", user.imageurl)
                Picasso.get().load(user.imageurl).placeholder(R.mipmap.ic_launcher)
                    .into(imageProfile)
                isFollowed(user.id, btnFollow)
                if (user.id == firebaseUser.uid) {
                    btnFollow.visibility = View.GONE
                }
                btnFollow.setOnClickListener {
                    if (btnFollow.text.toString() == "follow") {
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(firebaseUser.uid).child("following").child(user.id)
                            .setValue(true)
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(user.id).child("followers").child(firebaseUser.uid)
                            .setValue(true)
                    } else {
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(firebaseUser.uid).child("following").child(user.id).removeValue()
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(user.id).child("followers").child(firebaseUser.uid)
                            .removeValue()
                    }
                }
            }

        }

        private fun isFollowed(id: String, btnFollow: Button) {
            val reference =
                FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser.uid)
                    .child("following")
            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(id).exists())
                        btnFollow.text = "following"
                    else
                        btnFollow.text = "follow"
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding =
            UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val user = users[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return users.size
    }
}