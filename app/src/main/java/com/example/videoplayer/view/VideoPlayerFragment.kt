package com.example.videoplayer.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.videoplayer.databinding.FragmentVideoPlayerBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView

class VideoPlayerFragment : Fragment() {


    private var playerView: PlayerView? = null
    private var exoPlayer: SimpleExoPlayer? = null
    private var videoPath: String? = null
    private var binding: FragmentVideoPlayerBinding? = null

    companion object {
        private const val ARG_VIDEO_PATH = "video_path"
        fun newInstance(videoPath: String): VideoPlayerFragment {
            val fragment = VideoPlayerFragment()
            val args = Bundle()
            args.putString(ARG_VIDEO_PATH, videoPath)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        videoPath = arguments?.getString(ARG_VIDEO_PATH)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoPlayerBinding.inflate(LayoutInflater.from(context), container, false)
        playerView = binding?.videoPlayer
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializePlayer()
    }

    private fun initializePlayer() {
        exoPlayer = SimpleExoPlayer.Builder(requireContext()).build()
        playerView?.player = exoPlayer
        val mediaItem = videoPath?.let { MediaItem.fromUri(it) }
        if (mediaItem != null) {
            exoPlayer?.setMediaItem(mediaItem)
        }
        exoPlayer?.prepare()
        exoPlayer?.play()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        exoPlayer?.stop()
        exoPlayer?.release()
    }

    private fun checkAndroidVersionPermission() {
        // Request permission to access external storage if necessary
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE

        }
    }

}