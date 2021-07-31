/**
 * Created by Taiwo Farinu on 30-Jul-21
 */

package com.app.demo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.demo.R
import com.app.demo.databinding.CommentItemBinding
import com.app.demo.model.Comment
import com.app.demo.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class CommentAdapter(
    private var comments: List<Comment>,
    val firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!,
) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {


    inner class ViewHolder(private val itemBinding: CommentItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(comment: Comment) {
            itemBinding.comment.text = comment.comment
            FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)
                .addValueEventListener(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.getValue(User::class.java)
                            itemBinding.username.text = user?.username
                            if (user?.imageurl == "default") {
                                itemBinding.profileImage.setImageResource(R.mipmap.ic_launcher)
                            } else {
                                Picasso.get().load(user?.imageurl)
                                    .placeholder(R.mipmap.ic_launcher)
                                    .into(itemBinding.profileImage)
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding =
            CommentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int = comments.size


}