package com.example.helloworld.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.helloworld.data.Post
import com.example.helloworld.databinding.ItemPostBinding

class PostsAdapter(
    private val onEdit: (Post) -> Unit,
    private val onDelete: (Post) -> Unit
) : ListAdapter<Post, PostsAdapter.PostViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem
    }

    inner class PostViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.binding.textTitle.text = post.title
        holder.binding.textBody.text = post.body
        holder.binding.btnEdit.setOnClickListener { onEdit(post) }
        holder.binding.btnDelete.setOnClickListener { onDelete(post) }
    }
}
