package com.sap.imageclassifier.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sap.imageclassifier.R

class LibraryAdapter( val category: Category) : RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder>()  {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LibraryAdapter.LibraryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.catagory_card,
            parent, false
        )
        // at last we are returning our view holder
        // class with our item View File.
        return LibraryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LibraryAdapter.LibraryViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    class LibraryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
//        val courseNameTV: TextView = itemView.findViewById(R.id.idTVCourse)
//        val courseIV: ImageView = itemView.findViewById(R.id.idIVCourse)
    }

}