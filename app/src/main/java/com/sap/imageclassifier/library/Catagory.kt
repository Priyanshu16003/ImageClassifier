package com.sap.imageclassifier.library

import android.net.Uri

data class Category(
    var id : Int,
    var category : String,
    val imageUri : ArrayList<Uri>?
)
