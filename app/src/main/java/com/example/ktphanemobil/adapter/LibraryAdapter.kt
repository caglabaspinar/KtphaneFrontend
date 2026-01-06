package com.example.ktphanemobil.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ktphanemobil.R
import com.example.ktphanemobil.models.Library

class LibraryAdapter(
    private var libraries: List<Library>,
    private val onLibraryClick: (Library) -> Unit
) : RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.txtLibraryName)
        private val locationText: TextView = itemView.findViewById(R.id.txtLibraryLocation)

        fun bind(library: Library, onLibraryClick: (Library) -> Unit) {
            nameText.text = library.name
            locationText.text = library.location

            itemView.setOnClickListener {
                onLibraryClick(library)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_library, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(libraries[position], onLibraryClick)
    }

    override fun getItemCount(): Int = libraries.size

    fun updateList(newList: List<Library>) {
        libraries = newList
        notifyDataSetChanged()
    }
}
