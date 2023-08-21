package com.example.ambulance.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ambulance.databinding.ItemTextMessageBinding
import com.example.ambulance.model.TextMessages
import java.text.SimpleDateFormat

class CommentsAdapter(private val commentsList: List<TextMessages>) :
    RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = ItemTextMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentsList[position]
        holder.bind(comment)
    }

    override fun getItemCount(): Int {
        return commentsList.size
    }

    inner class CommentViewHolder(val binding: ItemTextMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: TextMessages) {
            val dateFormate = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT,SimpleDateFormat.SHORT)
            binding.tvDate.text = dateFormate.format(comment.timestamp)
            binding.tvMessage.text = comment.content
            binding.tvAuthor.text = comment.author
            // You can also display the timestamp if desired: itemView.tvTimestamp.text = ...
        }
    }
}