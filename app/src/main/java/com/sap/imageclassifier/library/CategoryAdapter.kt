package com.sap.imageclassifier.library

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sap.imageclassifier.R
import kotlin.reflect.KFunction1

class CategoryAdapter(val data: HashMap<Int, HashMap<String, Array<Uri>>>, private val onCardClick: KFunction1<Array<Uri>, Unit>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryNameTextView: TextView = itemView.findViewById(R.id.cat_name)
        val categoryImageView: ImageView = itemView.findViewById(R.id.image_view)
        val catagoryCard : CardView = itemView.findViewById(R.id.cardview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.catagory_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoryEntry = data.entries.elementAt(position)
        val categoryId = categoryEntry.key
        val categoryName = "Category $categoryId"
        val catName = categoryEntry.value.keys.toString()
        val catKey = catName.trim('[',']')
        val categoryImages = categoryEntry.value[catKey]

        holder.categoryNameTextView.text = catName.toString()
        holder.categoryImageView.setImageResource(R.drawable.ic_launcher_background)

            Glide.with(holder.itemView.context)
                .load(categoryImages?.get(0))
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.gallery_thumbnail)
                .into(holder.categoryImageView)

        holder.catagoryCard.setOnClickListener {
            categoryImages?.let { it1 -> onCardClick(it1) }
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }
}