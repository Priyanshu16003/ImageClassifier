package com.sap.imageclassifier

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.sap.imageclassifier.databinding.FragmentGalleryBinding
import kotlinx.coroutines.NonDisposableHandle.parent
import java.io.File
import java.io.FileNotFoundException



class GalleryFragment : Fragment() {
    private lateinit var binding: FragmentGalleryBinding

    private var imageUris: ArrayList<Uri> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_EXTERNAL_STORAGE_PERMISSION
            )
        } else {

            loadImage()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImage()
            } else {

                Toast.makeText(
                    requireContext(),
                    "External storage permission required to view images",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loadImage() {
        imageUris = getImagesFromExternalStorage() as ArrayList<Uri>
        binding.imageGridView.adapter = ImageAdapter(requireContext(), imageUris)
        binding.imageGridView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->

                val selectedImageUri = imageUris[position]


                val intent = Intent(requireContext(), ImageDialogActivity::class.java)
                intent.data = selectedImageUri

                startActivity(intent)
            }
    }



    private fun getImagesFromExternalStorage(): MutableList<Uri> {
        val imageUris = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        val selection = null
        val selectionArgs = null
        val queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursor = requireContext().contentResolver.query(
            queryUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                imageUris.add(contentUri)
            }
            cursor.close()
        }
        return imageUris
    }

    companion object {
        private const val REQUEST_EXTERNAL_STORAGE_PERMISSION = 100
    }
}

