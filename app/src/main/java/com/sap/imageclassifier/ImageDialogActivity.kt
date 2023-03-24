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


        val selectedImageUri = intent.data


        Glide.with(this)
            .load(selectedImageUri)
            .into(findViewById(R.id.imageView))
    }
}
