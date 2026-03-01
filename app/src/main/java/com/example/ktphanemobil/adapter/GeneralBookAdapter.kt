package com.example.ktphanemobil.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ktphanemobil.R
import com.example.ktphanemobil.models.Book

class GeneralBookAdapter(
    private val books: MutableList<Book>,
    private val isAdmin: Boolean,
    private val onBookClick: (Book) -> Unit,
    private val onDeleteClick: (Book) -> Unit
) : RecyclerView.Adapter<GeneralBookAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.txtBookTitle)
        private val authorText: TextView = itemView.findViewById(R.id.txtBookAuthor)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteGeneralBook)

        private val borrowStatusText: TextView = itemView.findViewById(R.id.txtBorrowStatus)
        fun bind(
            book: Book,
            isAdmin: Boolean,
            onBookClick: (Book) -> Unit,
            onDeleteClick: (Book) -> Unit
        ) {
            titleText.text = book.title
            authorText.text = book.author

            itemView.setOnClickListener { onBookClick(book) }

            if (isAdmin && (book.isBorrowed == true)) {
                borrowStatusText.visibility = View.VISIBLE
            } else {
                borrowStatusText.visibility = View.GONE
            }

            btnDelete.visibility = if (isAdmin) View.VISIBLE else View.GONE
            btnDelete.setOnClickListener { onDeleteClick(book) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_general_book, parent, false)
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
// Kitap listesini RecyclerView üzerinde gösteren adapter sınıfıdır.
// Admin ve Student rolüne göre silme butonunu ve ödünç durum bilgisini dinamik olarak yönetir,
// kitap tıklama ve silme işlemlerini callback fonksiyonları üzerinden ilgili ekrana iletir.