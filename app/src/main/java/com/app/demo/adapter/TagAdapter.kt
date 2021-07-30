/**
 * Created by Taiwo Farinu on 29-Jul-21
 */

package com.app.demo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.demo.databinding.TagItemBinding

class TagAdapter(private var tags: List<String>, private var tagsCount: List<String>) :
    RecyclerView.Adapter<TagAdapter.ViewHolder>() {

    inner class ViewHolder(private val itemBinding: TagItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(tag: String, tagsCount: String) {
            itemBinding.hashTag.text = "# $tag"
            itemBinding.noOfPosts.text = "$tagsCount posts"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding =
            TagItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tags[position], tagsCount[position])
    }

    override fun getItemCount(): Int {
        return tags.size.coerceAtLeast(tagsCount.size)
    }

    fun filter(filterTags: List<String>, filterTagsCount: List<String>) {
        this.tags = filterTags
        this.tagsCount = filterTagsCount

        notifyDataSetChanged()
    }

}