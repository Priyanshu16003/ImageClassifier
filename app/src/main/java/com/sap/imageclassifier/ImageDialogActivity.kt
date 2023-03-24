package com.sap.imageclassifier

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide

class ImageDialogActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_image_dialog)

        // Get the URI of the selected image from the Intent
        val selectedImageUri = intent.data

        // Load the selected image into the ImageView
        Glide.with(this)
            .load(selectedImageUri)
            .into(findViewById(R.id.imageView))
    }
}
