package com.example.videoplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoplayer.databinding.ItemVideoBinding
import com.example.videoplayer.interfaces.onItemClick
import com.example.videoplayer.model.Video

class VideoAdapter(
    val context: Context,
    private var musicList: List<Video>,
    private val onItemClick: onItemClick,
) :
    RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val musicList = musicList[position]
        holder.binding.videotitle.text = musicList.title
        Glide.with(context).load(musicList.url).thumbnail(0.1f).centerCrop()
            .into(holder.binding.imageView3)
        holder.itemView.setOnClickListener {
            onItemClick.onVideoClick(musicList)
        }
        holder.binding.videotitle.setOnClickListener {
            onItemClick.onTitleClick(musicList)
        }
        holder.binding.pdfshareimageView.setOnClickListener {
            onItemClick.onShareImgVClick(musicList)
        }
        holder.binding.pdfDeleteimageView.setOnClickListener {
            onItemClick.onDeleteImgVClick(musicList)
        }
        holder.itemView.setOnLongClickListener {
            onItemClick.onLongClickListener(musicList)
            true
        }
    }

    override fun getItemCount(): Int = musicList.size

    inner class VideoViewHolder(val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {}
}