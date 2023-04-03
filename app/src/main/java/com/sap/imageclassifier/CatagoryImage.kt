package com.sap.imageclassifier

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sap.imageclassifier.library.CategoryAdapter

class CatagoryImage : AppCompatActivity() {


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catagory_image)

        val uris: Array<Uri>? = intent.getParcelableArrayExtra("image")?.mapNotNull { it as? Uri }?.toTypedArray()
        val recyclerView = findViewById<RecyclerView>(R.id.imagerv_view)
        val adapter = uris?.let { ImageAdapter(it) }
        val layoutManager = GridLayoutManager(this, 3)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter


    }
}