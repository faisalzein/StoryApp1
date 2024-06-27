package com.example.storyapp.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.response.ListStoryItem


class StoryAdapter(

    private val storyList: List<ListStoryItem>,

    private val listener: OnAdapterListener
) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    interface OnAdapterListener {
        fun onClick(story: ListStoryItem)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_hero, parent, false)

        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = storyList[position]

        holder.bind(story)
        holder.itemView.setOnClickListener {
            listener.onClick(story)
        }
    }
    override fun getItemCount(): Int {
        return storyList.size
    }
    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.nameTextView)
        val photo: ImageView = itemView.findViewById(R.id.profileImageView)
        fun bind(story: ListStoryItem) {
            name.text = story.name
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .error(R.drawable.ic_launcher_background)
                .into(photo)
        }
    }
}
