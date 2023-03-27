package com.sap.imageclassifier

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.sap.imageclassifier.camera.CameraActivity
import com.sap.imageclassifier.databinding.ActivityMainBinding
import com.sap.imageclassifier.gallery.GalleryFragment
import com.sap.imageclassifier.library.LibraryFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.floatingActionButton.setOnClickListener{
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        val navController = findNavController(R.id.fragment)
        binding.bottomNavigationBar.setupWithNavController(navController)

    }
}