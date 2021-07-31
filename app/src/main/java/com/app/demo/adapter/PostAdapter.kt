/// Created by Taiwo Farinu on 30-Jul-21


package com.app.demo.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.demo.CommentActivity
import com.app.demo.R
import com.app.demo.databinding.PostItemBinding
import com.app.demo.model.Post
import com.app.demo.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class PostAdapter(
    private var posts: List<Post>,
    val firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!,
) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    inner class ViewHolder(private val itemBinding: PostItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(post: Post) {
            itemBinding.apply {
                Picasso.get().load(post.imageurl).into(postImage)
                description.text = post.description

                FirebaseDatabase.getInstance().reference.child("Users").child(post.publisher)
                    .addValueEventListener(
                        object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val user = dataSnapshot.getValue(User::class.java)
                                if (user?.imageurl == "default") {
                                    profileImage.setImageResource(R.mipmap.ic_launcher)
                                } else {
                                    Picasso.get().load(user?.imageurl)
                                        .placeholder(R.mipmap.ic_launcher)
                                        .into(profileImage)
                                }
                                username.text = user?.username
                                author.text = user?.name
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        })
                isLiked(post.postid, like)
                noOfLikes(post.postid, noOfLikes)
                getComments(post.postid, noOfComments)
                isSaved(post.postid, itemBinding.save)
                like.setOnClickListener {
                    if (like.tag == "like") {
                        FirebaseDatabase.getInstance().reference.child("Likes").child(post.postid)
                            .child(firebaseUser.uid).setValue(true)
                    } else {
                        FirebaseDatabase.getInstance().reference.child("Likes").child(post.postid)
                            .child(firebaseUser.uid).removeValue()
                    }
                }
                comment.setOnClickListener {
                    showComments(post)
                }
                noOfComments.setOnClickListener {
                    showComments(post)
                }
                save.setOnClickListener {
                    if (save.tag == "save") {
                        FirebaseDatabase.getInstance().reference.child("Saves")
                            .child(firebaseUser.uid)
                            .child(post.postid).setValue(true)
                    } else {
                        FirebaseDatabase.getInstance().reference.child("Saves")
                            .child(firebaseUser.uid)
                            .child(post.postid).removeValue()
                    }
                }
            }

        }

        private fun isSaved(postId: String, saveImageView: ImageView) {
            FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser.uid).addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.child(postId).exists()) {
                            itemBinding.save.setImageResource(R.drawable.ic_saved)
                            saveImageView.tag = "saved"
                        } else {
                            itemBinding.save.setImageResource(R.drawable.ic_save)
                            saveImageView.tag = "save"
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }

        private fun showComments(post: Post) {
            val context = itemBinding.root.context
            val intent = Intent(context, CommentActivity::class.java)
            intent.putExtra("postId", post.postid)
            intent.putExtra("authorId", post.publisher)
            context.startActivity(intent)
        }

        private fun getComments(postId: String, textView: TextView) {
            FirebaseDatabase.getInstance().reference.child("Comments").child(postId)
                .addValueEventListener(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            textView.text = "View All ${snapshot.childrenCount} Comments"
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
        }

        private fun isLiked(postId: String, imageView: ImageView) {
            FirebaseDatabase.getInstance().reference.child("Likes").child(postId)
                .addValueEventListener(
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.child(firebaseUser.uid).exists()) {
                                imageView.setImageResource(R.drawable.ic_liked)
                                imageView.tag = "liked"
                            } else {
                                imageView.setImageResource(R.drawable.ic_like)
                                imageView.tag = "like"
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })
        }

        private fun noOfLikes(postId: String, textView: TextView) {
            FirebaseDatabase.getInstance().reference.child("Likes").child(postId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val likes = snapshot.childrenCount
                        textView.text = "$likes likes"
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding =
            PostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size


}