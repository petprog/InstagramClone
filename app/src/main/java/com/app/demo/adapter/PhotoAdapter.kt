/**
 * Created by Taiwo Farinu on 31-Jul-21
 */

package com.app.demo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.demo.R
import com.app.demo.databinding.PhotoItemBinding
import com.app.demo.model.Post
import com.squareup.picasso.Picasso

class PhotoAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    inner class ViewHolder(private val itemBinding: PhotoItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(post: Post) {
            Picasso.get().load(post.imageurl).placeholder(R.mipmap.ic_launcher)
                .into(itemBinding.postImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding =
            PhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size
}