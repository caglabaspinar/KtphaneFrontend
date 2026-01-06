package com.example.ktphanemobil.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ktphanemobil.R
import com.example.ktphanemobil.models.Book

class LibraryBookAdapter(
    private val books: List<Book>,
    private val onBookClick: (Book) -> Unit
) : RecyclerView.Adapter<LibraryBookAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.txtLibBookTitle)
        private val authorText: TextView = itemView.findViewById(R.id.txtLibBookAuthor)

        fun bind(book: Book, onBookClick: (Book) -> Unit) {
            titleText.text = book.title
            authorText.text = book.author

            itemView.setOnClickListener {
                onBookClick(book)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_library_book, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(books[position], onBookClick)
    }

    override fun getItemCount(): Int = books.size

}
