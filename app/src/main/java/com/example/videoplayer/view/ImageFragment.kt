package com.example.videoplayer.view

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videoplayer.R
import com.example.videoplayer.adapter.Image

import com.example.videoplayer.databinding.FragmentImageBinding
import com.example.videoplayer.viewModel.ImageViewModel
import com.example.videoplayer.viewModel.MainActivityViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val READ_EXTERNAL_STORAGE_REQUEST = 1
private const val DELETE_PERMISSION_REQUEST = 2
class ImageFragment : Fragment() {
    private var binding: FragmentImageBinding? = null
    private val viewModel: ImageViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentImageBinding.inflate(LayoutInflater.from(context),container,false)

        // Switch to AppTheme for displaying the activity
        activity?.setTheme(R.style.AppTheme)
        val galleryAdapter = GalleryAdapter { image ->
            deleteImage(image)
        }

        binding?.imageGallery?.also { view ->
            view.layoutManager = GridLayoutManager(requireContext(), 2)
            view.adapter = galleryAdapter

        }

        viewModel.images.observe(requireActivity(), Observer<List<Image>> { images ->
            galleryAdapter.submitList(images)
            Toast.makeText(requireContext(), "${images.size}", Toast.LENGTH_SHORT).show()
        })

        viewModel.permissionNeededForDelete.observe(requireActivity(), Observer { intentSender ->
            intentSender?.let {
                startIntentSenderForResult(
                    intentSender,
                    DELETE_PERMISSION_REQUEST,
                    null,
                    0,
                    0,
                    0,
                    null
                )
            }
        })

        binding?.openAlbumButton?.setOnClickListener { openMediaStore() }
        binding?.grantPermissionButton?.setOnClickListener { openMediaStore() }

        if (!haveStoragePermission()) {
            binding?.albumContainer?.visibility = View.VISIBLE
        } else {
            showImages()
        }
        return binding?.root
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showImages()
                } else {
                    // If we weren't granted the permission, check to see if we should show
                    // rationale for the permission.
                    val showRationale =
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    if (showRationale) {
                        showNoAccess()
                    } else {
                        goToSettings()
                    }
                }
                return
            }
        }
    }



    //
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == DELETE_PERMISSION_REQUEST) {
            viewModel.deletePendingImage()
        }
    }

    private fun showImages() {
        viewModel.loadImages()
        binding?.albumContainer?.visibility = View.GONE
        binding?.permissionContainer?.visibility = View.GONE
    }

    private fun showNoAccess() {
        binding?.albumContainer?.visibility = View.GONE
        binding?.permissionContainer?.visibility = View.VISIBLE
    }

    private fun openMediaStore() {
        if (haveStoragePermission()) {
            showImages()
        } else {
            requestPermission()
        }
    }

    private fun goToSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${requireActivity().packageName}")).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.also { intent ->
            startActivity(intent)
        }
    }


    private fun haveStoragePermission() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED


    private fun requestPermission() {
        if (!haveStoragePermission()) {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissions,
                READ_EXTERNAL_STORAGE_REQUEST
            )
        }
    }

    private fun deleteImage(image: Image) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_dialog_title)
            .setMessage(getString(R.string.delete_dialog_message, image.displayName))
            .setPositiveButton(R.string.delete_dialog_positive) { _: DialogInterface, _: Int ->
                viewModel.deleteImage(image)
            }
            .setNegativeButton(R.string.delete_dialog_negative) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .show()
    }

    private inner class GalleryAdapter(val onClick: (Image) -> Unit) :
        ListAdapter<Image, AudioViewHolder>(Image.diffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.image_layout, parent, false)
            return AudioViewHolder(view, onClick)
        }

        override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
            val image = getItem(position)
            holder.rootView.tag = image
            holder.name.text = image.displayName

            Glide.with(holder.imageView)
                .load(image.contentUri)
                .thumbnail(0.33f)
                .centerCrop()
                .into(holder.imageView)
        }
    }
}

private class AudioViewHolder(view: View, onClick: (Image) -> Unit) :
    RecyclerView.ViewHolder(view) {
    val rootView = view
    val imageView: ImageView = view.findViewById(R.id.image)
    val name: TextView = view.findViewById(R.id.txtname)

    init {
        imageView.setOnClickListener {
            val image = rootView.tag as? Image ?: return@setOnClickListener
            onClick(image)
        }

    }
}