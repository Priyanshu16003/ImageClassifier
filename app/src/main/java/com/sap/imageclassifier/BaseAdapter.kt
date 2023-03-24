package com.sap.imageclassifier

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide


class ImageAdapter(private val context: Context, private val imagePaths: ArrayList<Uri>) : BaseAdapter() {

    override fun getCount(): Int {
        return imagePaths.size
    }

    override fun getItem(position: Int): Any {
        return imagePaths[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView: ImageView

        if (convertView == null) {
            imageView = ImageView(context)
            imageView.layoutParams = ViewGroup.LayoutParams(200, 200)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setPadding(8, 8, 8, 8)
        } else {
            imageView = convertView as ImageView
        }

        Glide.with(context).load(imagePaths[position]).into(imageView)

        return imageView
    }
}