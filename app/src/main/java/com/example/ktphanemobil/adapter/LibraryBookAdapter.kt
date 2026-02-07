package com.example.ktphanemobil.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ktphanemobil.R
import com.example.ktphanemobil.models.Book

class LibraryBookAdapter(
    private val books: MutableList<Book>,
    private val isAdmin: Boolean,
    private val onBookClick: (Book) -> Unit,
    private val onDeleteClick: (Book) -> Unit
) : RecyclerView.Adapter<LibraryBookAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.txtLibBookTitle)
        private val authorText: TextView = itemView.findViewById(R.id.txtLibBookAuthor)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteBook)

        fun bind(
            book: Book,
            isAdmin: Boolean,
            onBookClick: (Book) -> Unit,
            onDeleteClick: (Book) -> Unit
        ) {
            titleText.text = book.title
            authorText.text = book.author

            itemView.setOnClickListener { onBookClick(book) }

            // SADECE admin görsün
            btnDelete.visibility = if (isAdmin) View.VISIBLE else View.GONE
            btnDelete.setOnClickListener { onDeleteClick(book) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_library_book, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(books[position], isAdmin, onBookClick, onDeleteClick)
    }

    override fun getItemCount(): Int = books.size

    fun removeBookById(bookId: Int) {
        val index = books.indexOfFirst { it.id == bookId }
        if (index != -1) {
            books.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
