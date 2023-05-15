package com.example.videoplayer.interfaces

import com.example.videoplayer.model.Video

interface onItemClick {
  fun  onVideoClick(video: Video)
  fun onTitleClick(video: Video)
  fun onShareImgVClick(video: Video)
  fun onDeleteImgVClick(video: Video)
  fun onLongClickListener(video: Video)
}